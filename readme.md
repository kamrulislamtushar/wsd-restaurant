private void genFooter(PdfContentByte canvas, Document document, PdfWriter writer) {
    try {

        // ---------- CONSTANTS ----------
        float padding = 5f;
        float noteFontSize = 8f;
        float rectHeight = 16f;
        float imageHeight = 40f;

        // ---------- BASELINES ----------
        float pageBottom = document.bottom();

        // ---------- COPYRIGHT (PAGE BOTTOM CENTER) ----------
        float copyrightY = pageBottom - 5;

        ColumnText.showTextAligned(
                canvas,
                Element.ALIGN_CENTER,
                new Phrase(copyRight, new Font(Font.TIMES_ROMAN, 8)),
                document.getPageSize().getWidth() / 2,
                copyrightY,
                0
        );

        ColumnText.showTextAligned(
                canvas,
                Element.ALIGN_CENTER,
                new Phrase(copyRightTwo, new Font(Font.TIMES_ROMAN, 8)),
                document.getPageSize().getWidth() / 2,
                copyrightY - 10,
                0
        );

        // ---------- IMAGES (ABOVE COPYRIGHT) ----------
        float imageY = copyrightY + 15;

        Image imgLeft = Image.getInstance(pbeLogoPath);
        Image imgRight = Image.getInstance(pbeLogoPathTwo);

        imgLeft.scaleToFit(100, imageHeight);
        imgRight.scaleToFit(100, imageHeight);

        imgLeft.setAbsolutePosition(document.left(), imageY);
        imgRight.setAbsolutePosition(
                document.right() - imgRight.getScaledWidth(),
                imageY
        );

        canvas.addImage(imgLeft);
        canvas.addImage(imgRight);

        // ---------- FOOTER NOTE RECTANGLE (ABOVE IMAGES) ----------
        float rectBottom = imageY + imageHeight + 5;
        float rectTop = rectBottom + rectHeight;

        Rectangle rect = new Rectangle(
                document.left(),
                rectBottom,
                document.right(),
                rectTop
        );

        rect.setBorder(Rectangle.BOX);
        rect.setBorderWidth(0.5f);
        canvas.rectangle(rect);

        // ---------- FOOTER NOTE TEXT (INSIDE RECTANGLE) ----------
        ColumnText.showTextAligned(
                canvas,
                Element.ALIGN_LEFT,
                new Phrase(
                        footerNote1,
                        new Font(Font.TIMES_ROMAN, noteFontSize)
                ),
                document.left() + padding,
                rectTop - padding - noteFontSize,
                0
        );

    } catch (Exception e) {
        throw new RuntimeException("Footer generation failed", e);
    }
}
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

# Git Branching & Deployment Strategy

## Primary Branches

| Branch        | Purpose |
|---------------|---------|
| `main`        | Production-ready code. Only production-approved features/hotfixes are merged here. |
| `staging`     | Pre-production/sanity environment. Used to test production copy + latest approved features. |
| `development` | UAT/QA branch. Feature branches are deployed here for QA testing before production. |

## Supporting Branches

- **Feature branches**: `feature/<feature-name>`
    - Created from `development`.
    - For new features or enhancements.
- **Hotfix branches**: `hotfix/<issue-name>`
    - Created from `main` for production issues.

## Branch Flow

1. **Feature Branch Development**
    - Developers create a feature branch from `development`.
    - Commit work and push to remote.

2. **QA / UAT Testing**
    - Deploy the feature branch to **development environment (UAT/QA)** for testing.
    - Fix any issues directly in the feature branch.

3. **Pre-Production / Staging**
    - Deploy approved feature branches to **staging environment** for sanity testing.
    - Staging environment is production-like; it is **not a branch**.

4. **Production Deployment**
    - Merge the **feature branch directly into `main`** after QA and staging verification.
    - CI/CD deploys `main` to production.
    - Optionally, merge `main` back into `development` to keep branches in sync.

5. **Hotfix Workflow**
    - Hotfix branches are created from `main`.
    - Fix issues and deploy to **staging** for verification.
    - Merge hotfix into `main` (production) and then into `development` to stay updated.

## Key Rules

- Never merge `development` into `main`. Only production-approved feature/hotfix branches are merged into `main`.
- `staging` is an environment, not a branch.
- Include issue IDs in branch names for traceability (e.g., `feature/1234-duplicate-login`).
- Hotfix branches always start from `main` and must be merged back into both `main` and `development`.

set JAVA_OPTIONS=%JAVA_OPTIONS% -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005

Here’s a straight‑up, no‑BS vulnerability assessment of the dependencies you listed. I’ve grouped them by category, called out what kind of issues are known, and pointed out versions you should upgrade to — especially because you’re stuck on Java 8 and many old libs there won’t get fixes anymore.
Security‑Critical Dependencies (Definite Vulnerabilities)
Spring Framework (4.3.6.RELEASE + related Spring Security 4.2.1)
This entire line is long out of support and has multiple vulnerabilities, including directory traversal and Cross‑Site Tracing issues. Spring 4.3.x before ~4.3.18–4.3.20 has known CVEs — including high/medium ones affecting input validation and HTTP handling. �
So:
• Affected by cross‑site tracing / XST and other issues on older Spring 4.3.x. �
Recommendation: Move at least to Spring 4.3.29.RELEASE (but even that is EOL) or better yet Spring 5.x compliant with Java 8. (Spring 5 still lots more fixes.)
Home +1
Home
com.fasterxml.jackson.core:jackson‑databind:2.8.6
This version has tons of CVEs across many years (over 50 listed issues with deserialization and input handling). �
The infamous deserialization remote code execution issues in this lineage include CVE‑2017‑7525 / CVE‑2017‑17485 / CVE‑2017‑15095 and many more. �
So: This version cannot be trusted for untrusted JSON input.
Recommendation: Upgrade at least to the latest 2.8 branch patch – 2.8.11.6 or to 2.9 / 2.10+ as possible. Better if you can push to Jackson 2.13+ for ongoing support.
Maven Repository
CVE Details +1
org.hibernate:hibernate‑core:5.4.0.Final
There are high‑severity SQL injection related flaws in 5.4.y series before ~5.4.18 and ~5.4.24. �
Recommendation: Upgrade to at least 5.4.24.Final or beyond, ideally the last 5.4 release.
VulnInfo Guide
Medium / Noticeable Issues You Should Address
org.apache.httpcomponents:httpclient:4.5.3
Old HttpClient versions have improper input validation, DoS and uri misinterpretation issues. Known issues persist in the whole 4.5.x range up through 4.5.12. �
Recommendation: Upgrade to 4.5.14 (latest in 4.5 family) if you must stay Java 8, or to 5.x on higher Java.
VulnInfo Guide
Apache Commons FileUpload (1.3.2)
Versions before 1.6.0 have Denial‑of‑Service and header handling issues. �
Recommendation: Move to the latest 1.x (≥1.6.0) where these are mitigated, or to the 2.x branch.
commons.apache.org
Older Libraries with Known or Likely Issues
org.apache.poi:3.14
Version 3.14 has documented XXE / XML parsing and DoS issues (older CVEs). �
Recommendation: Upgrade POI to a maintained branch (≥5.2.x) that fixes these parser issues.
CVE Details +1
com.itextpdf.tool:5.5.11
iText5 is end of life and has vulnerabilities related to BouncyCastle and potentially XML security components. �
Recommendation: Migrate off iText 5 to a supported series (8.x or commercial release), or at least bump to the last patch in the 5 branch.
kb.itextpdf.com
net.sf.ehcache:2.10.3
No direct CVEs in that exact version, but it shadows older Jackson/Jetty classes that have issues inside the jar. �
Recommendation: At least bring this up to 2.10.9.2 or later supported branch.
VulnInfo Guide +1
org.slf4j:1.7.23
SLF4J itself isn’t normally vulnerable, but older logging libs have seen theoretical issues — update to 1.7.36+ if possible.
logback (1.2.1)
Old; while no “massive exploitation” record, best to bump to 1.2.11 or later.
javax.validation:1.1.0.Final, javax.servlet:3.0.1
These are ancient specs; Java EE has evolved. They don’t have active CVEs like serialization libs, but you lose security updates in old API implementations.
AspectJ 1.8.10, junit 4.12, freemarker 2.3.25, etc.
Test and tool libs are rarely a direct runtime risk unless exposed, but being eons old means more latent issues. Upgrade where possible.
Summary — Priority Ranking
High urgency (major security issues):
• Jackson‑databind 2.8.6 — huge CVE history. �
• Spring 4.3.6 / Spring Security 4.2.1 — known CVEs and EOL. �
• Hibernate 5.4.0 — SQL injection risk. �
Maven Repository
Home
VulnInfo Guide
Medium urgency:
• HttpClient 4.5.3 — improper input validation. �
• Commons FileUpload pre‑1.6 — DoS. �
• POI 3.14 — XXE/DoS. �
• iText 5.5.11 — EOL & dependency vulnerabilities. �
VulnInfo Guide
commons.apache.org
CVE Details
kb.itextpdf.com
Lower but out‑of‑date:
• Logging (SLF4J/Logback), validation/servlet APIs, freemarker — not zero risk but less explosive.
Real Advice (not sugar‑coating)
Running Java 8 with these old libs is straight riskier than a stockpile of expired medicine. Many of these versions are EOL and will never get fixes. Get them updated — and for Spring especially, consider at least Spring 5.x (maintained on Java 8) or bump Java runtime where possible. Static analysis / dependency scanners (OWASP Dependency‑Check, Snyk, GitHub alerts) will flag dozens more if you let them chew on this stack.
If you want a tailored upgrade path with minimum breaking changes, I can draft one next — including specific target versions compatible with Java 8.
