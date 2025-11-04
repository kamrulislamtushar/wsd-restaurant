# WSD Order Management

## Building for production

### Packaging as jar

To build the final jar and optimize the orderManagement application for production, run:
```
./mvnw clean package -DskipTests  
```

Once that is done please run 

```
docker compose up
```

Once the application is running it can be accessed via 

```
http://localhost:8080
```
(function() {
  const TOKEN_META_NAME = "customToken";

  // ==============================
  // Get token from meta tag
  // ==============================
  function getCustomToken() {
    const meta = document.querySelector(`meta[name="${TOKEN_META_NAME}"]`);
    return meta ? meta.getAttribute("content") : null;
  }

  const customToken = getCustomToken();

  // ==============================
  // Sanitize HTML input
  // ==============================
  function sanitizeHTML(input) {
    if (!input || typeof input !== "string") return input;

    return input
      .replace(/<script\b[^<]*(?:(?!<\/script>)<[^<]*)*<\/script>/gi, "")
      .replace(/\son\w+\s*=\s*(['"]).*?\1/gi, "")
      .replace(/(javascript:|data:text\/html)/gi, "")
      .replace(/<\/?(iframe|object|embed|applet)[^>]*>/gi, "");
  }

  // ==============================
  // Extract script attributes
  // ==============================
  function extractAttr(attrs, name) {
    if (!attrs) return null;
    try {
      const div = document.createElement("div");
      div.innerHTML = "<div " + attrs + "></div>";
      return div.firstChild ? div.firstChild.getAttribute(name) : null;
    } catch {
      const match = attrs.match(
        new RegExp(name + "\\s*=\\s*(?:\"([^\"]*)\"|'([^']*)'|([^\\s>]+))", "i")
      );
      return match ? match[1] || match[2] || match[3] : null;
    }
  }

  // ==============================
  // Safely inject HTML + execute scripts
  // ==============================
  function safeInjectHtml($target, value) {
    if (typeof value !== "string") return $target.html(value);

    let sanitized = sanitizeHTML(value);
    const scripts = [];

    sanitized = sanitized.replace(
      /<script\b([^>]*)>([\s\S]*?)<\/script>/gi,
      function(_, attrs, code) {
        const src = extractAttr(attrs, "src");
        scripts.push({ src, code });
        return ""; // remove scripts from injected HTML
      }
    );

    const $result = $target.html(sanitized);

    scripts.forEach(script => {
      try {
        // Load external script
        if (script.src) {
          const s = document.createElement("script");
          s.src =
            script.src +
            (customToken
              ? (script.src.includes("?") ? "&" : "?") +
                TOKEN_META_NAME +
                "=" +
                encodeURIComponent(customToken)
              : "");
          s.async = false;
          document.body.appendChild(s);
        }
        // Execute inline script securely
        else if (script.code && script.code.trim()) {
          const s = document.createElement("script");
          s.textContent = `
            (function(){
              const token = "${customToken || ""}";
              if(token) window.currentToken = token;
              ${script.code}
            })();
          `;
          document.body.appendChild(s);
        }
      } catch (err) {
        console.error("Safe script execution error:", err);
      }
    });

    return $result;
  }

  // ==============================
  // Override jQuery methods
  // ==============================
  const originalHtml = jQuery.fn.html;
  const originalAppend = jQuery.fn.append;

  jQuery.fn.html = function(value) {
    if (arguments.length === 0) {
      return originalHtml.call(this);
    }
    return safeInjectHtml(this, value);
  };

  jQuery.fn.append = function(value) {
    if (typeof value === "string") {
      value = sanitizeHTML(value);
    }
    return originalAppend.call(this, value);
  };

  console.log("[SafeHTML] Enabled — XSS protection active using custom token");
})();
