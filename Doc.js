(function ($) {
  const _oldHtml = $.fn.html;

  // Define known-safe script sources (prefix or domain)
  const SAFE_SCRIPT_PATTERNS = [
    /^\/js\//,
    /^\/static\/js\//,
    /^https:\/\/cdn\.jsdelivr\.net\//,
    /^https:\/\/cdnjs\.cloudflare\.com\//,
    /^https:\/\/ajax\.googleapis\.com\//,
    /^https:\/\/([a-z0-9-]+\.)*xy\.com\//i  // allow any subdomain of xy.com
  ];

  // Allow specific inline script naming patterns
  const SAFE_INLINE_PATTERNS = [
    /^init[A-Z]/,
    /^load[A-Z]/
  ];

  $.fn.html = function (value) {
    if (typeof value === 'string') {
      const $temp = $('<div>').append($.parseHTML(value, document, true));

      // Sanitize scripts
      $temp.find('script').each(function () {
        const src = $(this).attr('src');
        const js = $(this).text();

        if (src) {
          const isSafe = SAFE_SCRIPT_PATTERNS.some(pattern => pattern.test(src));
          if (!isSafe) {
            console.warn('[XSSGuard] Blocked script source:', src);
            $(this).remove();
          }
        } else if (js) {
          const matches = SAFE_INLINE_PATTERNS.some(pat => pat.test(js.trim()));
          if (!matches) {
            console.warn('[XSSGuard] Blocked inline script:', js.substring(0, 60) + '...');
            $(this).remove();
          }
        }
      });

      const result = _oldHtml.call(this, $temp.html());

      // Re-execute allowed scripts manually
      $temp.find('script').each(function () {
        const src = $(this).attr('src');
        const js = $(this).text();

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

    // Default .html() behavior for getters
    return _oldHtml.apply(this, arguments);
  };
})(jQuery);
