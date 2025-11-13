(function ($) {
  const _oldHtml = $.fn.html;

  // Detect full context path automatically based on /resources/
  const CONTEXT_PATH = (function () {
    const scripts = document.querySelectorAll('script[src]');
    for (const s of scripts) {
      const src = s.getAttribute('src');
      if (src && src.includes('/resources/')) {
        return src.split('/resources/')[0]; // everything before /resources/
      }
    }

    // Fallback: try from current path
    const path = window.location.pathname;
    if (path.includes('/resources/')) {
      return path.split('/resources/')[0];
    }

    return ''; // default to root if nothing matches
  })();

  // Define allowed script patterns
  const SAFE_SCRIPT_PATTERNS = [
    new RegExp(`^${CONTEXT_PATH}/resources/js/`),
    /^https:\/\/cdn\.jsdelivr\.net\//,
    /^https:\/\/cdnjs\.cloudflare\.com\//,
    /^https:\/\/ajax\.googleapis\.com\//,
    /^https:\/\/([a-z0-9-]+\.)*xy\.com\//i
  ];

  // Allowed inline script patterns (optional, adjust as needed)
  const SAFE_INLINE_PATTERNS = [
    /^init[A-Z]/,
    /^load[A-Z]/
  ];

  $.fn.html = function (value) {
    if (typeof value === 'string') {
      const parser = new DOMParser();
      const doc = parser.parseFromString(value, 'text/html');
      const $temp = $(doc.body);

      // Sanitize scripts
      $temp.find('script').each(function () {
        const src = this.getAttribute('src');
        const js = this.textContent;

        if (src) {
          const isSafe = SAFE_SCRIPT_PATTERNS.some(p => p.test(src));
          if (!isSafe) {
            console.warn('[XSSGuard] Blocked script source:', src);
            this.remove();
          }
        } else if (js) {
          const matches = SAFE_INLINE_PATTERNS.some(p => p.test(js.trim()));
          if (!matches) {
            console.warn('[XSSGuard] Blocked inline script:', js.substring(0, 60) + '...');
            this.remove();
          }
        }
      });

      const result = _oldHtml.call(this, $temp.html());

      // Re-execute allowed scripts
      $temp.find('script').each(function () {
        const src = this.getAttribute('src');
        const js = this.textContent;

        if (src) {
          $.getScript(src).fail(() =>
              console.error('[XSSGuard] Failed to load script:', src)
          );
        } else if (js) {
          try {
            new Function(js)();
          } catch (e) {
            console.error('[XSSGuard] Script exec error:', e);
          }
        }
      });

      return result;
    }

    return _oldHtml.apply(this, arguments);
  };
})(jQuery);
