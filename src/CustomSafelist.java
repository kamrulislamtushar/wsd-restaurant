package com.legacy.filter;

import org.jsoup.safety.Safelist;

/**
 * STRICT SAFELIST - No HTML allowed
 *
 * Since your application never needs HTML/script as input,
 * we strip ALL HTML tags and only allow plain text.
 *
 * This is MUCH more secure than the permissive safelist.
 */
public class CustomSafelist {

    private static Safelist instance;

    public static Safelist get() {
        if (instance == null) {
            instance = createStrictSafelist();
        }
        return instance;
    }

    /**
     * STRICT MODE: No HTML tags allowed at all
     * All tags are stripped, only plain text remains
     */
    private static Safelist createStrictSafelist() {
        // Safelist.none() = strips ALL HTML tags, keeps only text
        return Safelist.none();
    }

    /**
     * Alternative: If you need BASIC formatting only (bold, italic, links)
     * Uncomment this method and use it in createStrictSafelist()
     */
    private static Safelist createBasicSafelist() {
        // Safelist.basic() allows: b, em, i, strong, u, a, br, p
        // NO script, style, div, span, etc.
        Safelist safelist = Safelist.basic();

        // If you want links, configure them safely
        safelist.addProtocols("a", "href", "http", "https", "mailto");

        return safelist;
    }

    /**
     * For HTML fragments (JSPs loaded via AJAX into #main-content)
     * Preserves form structure but strips dangerous content
     */
    public static Safelist getForFragments() {
        Safelist safelist = new Safelist();

        // Allow structure tags (NO script, style, or meta)
        safelist.addTags("div", "span", "p", "br",
                "table", "thead", "tbody", "tr", "th", "td",
                "ul", "ol", "li", "section", "article", "header", "footer");

        // Text formatting
        safelist.addTags("b", "i", "u", "strong", "em", "small",
                "h1", "h2", "h3", "h4", "h5", "h6", "hr");

        // Links and images (safe protocols only)
        safelist.addTags("a", "img");
        safelist.addAttributes("a", "href", "title", "target");
        safelist.addProtocols("a", "href", "http", "https", "#");
        safelist.addAttributes("img", "src", "alt", "width", "height");
        safelist.addProtocols("img", "src", "http", "https", "data");

        // Form elements (your JSPs need these)
        safelist.addTags("form", "input", "select", "option", "textarea", "label", "button");
        safelist.addAttributes("form", "action", "method", "id", "name", "class");
        safelist.addAttributes("input", "type", "name", "value", "placeholder",
                "checked", "id", "class", "disabled", "readonly", "required");
        safelist.addAttributes("select", "name", "id", "class", "multiple");
        safelist.addAttributes("option", "value", "selected");
        safelist.addAttributes("textarea", "name", "rows", "cols", "placeholder", "id", "class");
        safelist.addAttributes("button", "type", "name", "value", "id", "class");

        // Global attributes (for your SPA functionality)
        safelist.addAttributes(":all", "id", "class", "style", "title", "data-*", "aria-*");

        // NO script, link, style, meta tags allowed in fragments

        return safelist;
    }
    public static Safelist getForFullPageResponse() {
        Safelist safelist = new Safelist();

        // Full HTML document structure
        safelist.addTags("html", "head", "body", "div", "span", "p", "br",
                "table", "thead", "tbody", "tr", "th", "td",
                "ul", "ol", "li", "section", "article", "header", "footer", "nav", "main");

        // Text formatting
        safelist.addTags("b", "i", "u", "strong", "em", "small", "h1", "h2", "h3", "h4", "h5", "h6",
                "blockquote", "pre", "code", "hr");

        // CRITICAL: Scripts, styles, meta for full pages
        safelist.addTags("meta", "title", "script", "link", "style");
        safelist.addAttributes("meta", "charset", "name", "content", "http-equiv");
        safelist.addAttributes("script", "src", "type", "async", "defer");
        safelist.addAttributes("link", "href", "rel", "type", "media");
        safelist.addProtocols("script", "src", "http", "https");
        safelist.addProtocols("link", "href", "http", "https");

        // Links and images
        safelist.addTags("a", "img");
        safelist.addAttributes("a", "href", "title", "target", "rel");
        safelist.addProtocols("a", "href", "http", "https", "mailto", "#");
        safelist.addAttributes("img", "src", "alt", "width", "height");
        safelist.addProtocols("img", "src", "http", "https", "data");

        // Form elements
        safelist.addTags("form", "input", "select", "option", "textarea", "label", "button");
        safelist.addAttributes("form", "action", "method", "id", "name", "class");
        safelist.addAttributes("input", "type", "name", "value", "placeholder",
                "checked", "id", "class", "disabled", "readonly", "required");
        safelist.addAttributes("select", "name", "id", "class", "multiple");
        safelist.addAttributes("option", "value", "selected");
        safelist.addAttributes("textarea", "name", "rows", "cols", "placeholder", "id", "class");
        safelist.addAttributes("button", "type", "name", "value", "id", "class");

        // Global attributes (for SPA)
        safelist.addAttributes(":all", "id", "class", "style", "title", "data-*", "aria-*", "role");

        return safelist;
    }
}