(function($) {
  const TOKEN_META_NAME = "customToken";

  // Read token from meta tag once
  function getCustomToken() {
    const meta = document.querySelector(`meta[name="${TOKEN_META_NAME}"]`);
    return meta ? meta.getAttribute("content") : null;
  }
  const customToken = getCustomToken();

  // Helper: extract attribute safely from attrs string
  function extractAttr(attrs, name) {
    if (!attrs) return null;
    try {
      const wrapper = document.createElement("div");
      wrapper.innerHTML = "<div " + attrs + "></div>";
      return wrapper.firstChild ? wrapper.firstChild.getAttribute(name) : null;
    } catch (e) {
      const match = attrs.match(
        new RegExp(name + "\\s*=\\s*(?:\"([^\"]*)\"|'([^']*)'|([^\\s>]+))", "i")
      );
      return match ? (match[1] || match[2] || match[3]) : null;
    }
  }

  // Lightweight sanitizer for the fragment (remove inline event attributes and dangerous tags/URLs)
  function sanitizeHTMLFragment(input) {
    if (!input || typeof input !== "string") return input;
    return input
      // remove script tags (we will extract them separately if needed)
      .replace(/<script\b[^<]*(?:(?!<\/script>)<[^<]*)*<\/script>/gi, "")
      // remove inline event handlers like onclick="..."
      .replace(/\son\w+\s*=\s*(['"])[\s\S]*?\1/gi, "")
      // remove javascript: and data:text/html protocols
      .replace(/(javascript:|data:text\/html)/gi, "")
      // remove dangerous tags like iframe/object/embed/applet
      .replace(/<\/?(iframe|object|embed|applet)[^>]*>/gi, "");
  }

  // Extract scripts from HTML (return sanitizedHtml without script tags, and array of script objects)
  function extractScripts(html) {
    const scripts = [];
    if (!html) return { html: "", scripts };

    // We will build the sanitized html by replacing script tags with placeholders, then remove them
    // Simpler: iterate regex and push scripts, then remove script tags.
    html.replace(/<script\b([^>]*)>([\s\S]*?)<\/script>/gi, function(_, attrs, code) {
      const src = extractAttr(attrs, "src");
      const token = extractAttr(attrs, TOKEN_META_NAME);
      scripts.push({ src: src ? src.trim() : null, code: code ? code.trim() : null, token });
      return ""; // not used, but safe
    });

    // Remove script tags from html
    const cleaned = html.replace(/<script\b[^<]*(?:(?!<\/script>)<[^<]*)*<\/script>/gi, "");
    return { html: cleaned, scripts };
  }

  // Execute inline script code safely, without triggering .html override
  function executeInlineScriptCode(code, token) {
    try {
      // Wrap in IIFE so we don't leak variables; also set currentToken if available
      const wrapper = document.createElement("script");
      wrapper.type = "text/javascript";
      // embed token into the wrapper execution scope
      wrapper.textContent = `(function(){ var __APP_TOKEN = ${JSON.stringify(token || "")}; (function(){ ${code} })(); })();`;
      document.body.appendChild(wrapper);
    } catch (e) {
      console.error("Error executing inline script:", e);
    }
  }

  // Append external script element (adds token as query param if present)
  function appendExternalScript(src, token) {
    try {
      const el = document.createElement("script");
      // Append token as query param so the backend (if needed) can validate requests from scripts
      if (token) {
        const sep = src.indexOf("?") === -1 ? "?" : "&";
        el.src = src + sep + TOKEN_META_NAME + "=" + encodeURIComponent(token);
      } else {
        el.src = src;
      }
      el.async = false; // preserve execution order
      document.body.appendChild(el);
    } catch (e) {
      console.error("Error appending external script:", e);
    }
  }

  // STORE ORIGINALS to avoid recursion
  const originalHtml = $.fn.html;
  const originalAppend = $.fn.append;

  // The safe injection logic — uses originalHtml to insert HTML so it doesn't recurse
  function safeInjectHtml($collection, value) {
    // extract scripts first from the raw value (so sanitizer doesn't remove them if we decide to keep)
    const extracted = extractScripts(value);
    // sanitize the remainder
    const sanitizedFragment = sanitizeHTMLFragment(extracted.html);

    // Inject sanitized fragment into each element in the collection using originalHtml to avoid recursion
    // originalHtml called on $collection will set content for all matched elements (preserves jQuery behavior)
    originalHtml.call($collection, sanitizedFragment);

    // Now handle scripts in order
    extracted.scripts.forEach(script => {
      // If script has src -> append external script
      if (script.src) {
        // no domain check requested — append directly (with token if present)
        appendExternalScript(script.src, customToken);
      } else if (script.code && script.code.trim()) {
        // Inline script: execute only when token matches or no token declared on script
        // NOTE: because you set token server-side as data attribute or meta, we can accept scripts
        // that come from server-rendered fragments. If your server adds token attribute to legitimate script tags,
        // you may check script.token here. For now we execute them unconditionally but with token injected.
        executeInlineScriptCode(script.code, customToken);
      }
    });

    // return the collection to preserve chainability
    return $collection;
  }

  // OVERRIDES
  $.fn.html = function(value) {
    // getter: return original behavior
    if (arguments.length === 0) {
      return originalHtml.call(this);
    }
    // setter: use safe inject
    return safeInjectHtml(this, value);
  };

  $.fn.append = function(value) {
    // If string, sanitize first (and extract scripts)
    if (typeof value === "string") {
      // reuse safe injection by wrapping into a temporary container and appending
      // but to preserve append semantics on collections, we can call safeInjectHtml on a detached jQuery object
      // and then append its children to each element.
      const temp = $("<div>");
      safeInjectHtml(temp, value);
      // append children of temp to each element in the original collection
      return this.each(function() {
        const $this = $(this);
        temp.children().each(function() {
          $this.append(this); // this uses original jQuery append for DOM nodes (no recursion)
        });
      });
    } else {
      // non-string values: let original append handle (DOM nodes, jQuery objects)
      return originalAppend.call(this, value);
    }
  };

  // log for debug
  if (window.console && console.info) {
    console.info("[SafeHTML] jQuery .html()/.append() overridden (recursion fixed)");
  }

})(jQuery);
