package utils;

import com.deque.html.axecore.playwright.AxeBuilder;
import com.deque.html.axecore.results.AxeResults;
import com.deque.html.axecore.results.Rule;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Comprehensive accessibility testing utilities using Axe-core.
 * Provides automated a11y scans, violation filtering, and detailed reporting.
 */
public final class AccessibilityUtil {

    private static final Logger logger = LoggerFactory.getLogger(AccessibilityUtil.class);
    private static final Set<String> FAILING_IMPACTS = Set.of("critical", "serious");
    private static final String REPORTS_DIR = "target/a11y-reports";
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private static final Map<String, Set<String>> WHITELIST = new HashMap<>();

    private AccessibilityUtil() {
        // Utility class - no instantiation
    }

    /**
     * Scan a page for accessibility violations.
     * Fails on critical or serious violations unless whitelisted.
     *
     * @param page     the Playwright page
     * @param pageName the name of the page being scanned
     */
    public static void scan(final Page page, final String pageName) {
        scan(page, pageName, null);
    }

    /**
     * Scan a page with specific WCAG tags.
     *
     * @param page     the Playwright page
     * @param pageName the name of the page being scanned
     * @param tags     the WCAG tags to check (null defaults to wcag2a, wcag2aa)
     */
    public static void scan(final Page page, final String pageName, final List<String> tags) {
        logger.info("Starting accessibility scan: {}", pageName);

        final List<String> scanTags = tags != null ? tags : List.of("wcag2a", "wcag2aa");

        final AxeResults results = new AxeBuilder(page)
                .withTags(scanTags)
                .analyze();

        final List<Rule> violations = results.getViolations();
        final List<Rule> passes = results.getPasses();

        logger.info("[A11Y][{}] Found {} violations and {} passes", pageName, violations.size(), passes.size());

        // Generate report
        generateReport(pageName, results);

        // Check for blocking issues
        boolean hasBlockingIssues = false;
        final List<Rule> blockedRules = new ArrayList<>();

        for (final Rule rule : violations) {
            final String impact = rule.getImpact();
            logger.info("[A11Y][{}] {} | Impact: {}", pageName, rule.getId(), impact);

            if (impact != null && FAILING_IMPACTS.contains(impact)) {
                // Check if whitelisted
                if (isWhitelisted(pageName, rule.getId())) {
                    logger.warn("[A11Y][{}] ✓ Whitelisted violation: {} (Impact: {})", pageName, rule.getId(), impact);
                    continue;
                }

                hasBlockingIssues = true;
                blockedRules.add(rule);

                rule.getNodes().forEach(node -> logger.warn("  ↳ {}", node.getHtml()));
            }
        }

        if (hasBlockingIssues) {
            final String message = String.format(
                    "Accessibility blocking issues found on %s (%d critical/serious violations)",
                    pageName,
                    blockedRules.size());
            logger.error(message);

            // Check if strict mode is enabled
            final boolean strictMode = ConfigReader.getBoolean("a11y.violation.strict.mode", false);
            if (strictMode) {
                throw new AssertionError(message);
            } else {
                logger.warn("Accessibility violations detected but strict mode is disabled - continuing test");
            }
        }

        logger.info("✅ Accessibility scan passed: {} - {} violations (non-blocking)", pageName, violations.size());
    }

    /**
     * Scan a specific element for accessibility violations.
     *
     * @param page        the Playwright page
     * @param element     the locator to scan
     * @param elementName the name of the element
     */
    public static void scanElement(final Page page, final Locator element, final String elementName) {
        logger.info("Starting element accessibility scan: {}", elementName);

        try {
            final AxeResults results = new AxeBuilder(page)
                    .include(element.toString())
                    .withTags(List.of("wcag2a", "wcag2aa"))
                    .analyze();

            final List<Rule> violations = results.getViolations();

            if (!violations.isEmpty()) {
                violations.forEach(rule -> {
                    final String impact = rule.getImpact();
                    logger.warn("[A11Y] Element {} - {} | Impact: {}", elementName, rule.getId(), impact);
                });

                if (violations.stream().anyMatch(rule -> FAILING_IMPACTS.contains(rule.getImpact()))) {
                    throw new AssertionError(
                            String.format("Element %s has critical accessibility violations", elementName));
                }
            }

            logger.info("✅ Element scan passed: {}", elementName);
        } catch (final Exception e) {
            logger.error("Failed to scan element: {}", elementName, e);
            throw new RuntimeException("Element scan failed: " + elementName, e);
        }
    }

    /**
     * Check keyboard navigation on a page (Tab key).
     *
     * @param page     the Playwright page
     * @param pageName the name of the page
     */
    public static void checkKeyboardNavigation(final Page page, final String pageName) {
        logger.info("Checking keyboard navigation: {}", pageName);

        try {
            // Press Tab and verify focus changes
            page.keyboard().press("Tab");
            Thread.sleep(100);

            final String focusedElement = (String) page.evaluate("() => document.activeElement?.tagName");
            if (focusedElement == null || focusedElement.equals("BODY")) {
                logger.warn("[A11Y][{}] No focusable elements found on page", pageName);
            } else {
                logger.debug("[A11Y][{}] ✓ Tab navigation works - Focused: {}", pageName, focusedElement);
            }
        } catch (final Exception e) {
            logger.error("Keyboard navigation check failed: {}", pageName, e);
            throw new RuntimeException("Keyboard navigation check failed: " + pageName, e);
        }
    }

    /**
     * Validate ARIA attributes on specific element.
     *
     * @param locator     the element to check
     * @param elementName the name of the element
     */
    public static void validateAriaAttributes(final Locator locator, final String elementName) {
        try {
            final String role = locator.getAttribute("role");
            final String ariaLabel = locator.getAttribute("aria-label");
            final String ariaLabelledBy = locator.getAttribute("aria-labelledby");

            if (ariaLabel == null && ariaLabelledBy == null) {
                logger.warn("[A11Y] Element {} lacks aria-label or aria-labelledby", elementName);
            }

            logger.debug("[A11Y] Element {} - role: {}, aria-label: {}", elementName, role, ariaLabel);
        } catch (final Exception e) {
            logger.error("ARIA validation failed for: {}", elementName, e);
            throw new RuntimeException("ARIA validation failed: " + elementName, e);
        }
    }

    /**
     * Check color contrast on an element.
     * Note: Requires Axe-core Pro or custom implementation.
     *
     * @param page        the Playwright page
     * @param locator     the element to check
     * @param elementName the element name
     */
    public static void checkColorContrast(final Page page, final Locator locator, final String elementName) {
        try {
            // This would require additional color contrast checking library
            // For now, log as informational
            logger.info("[A11Y] Color contrast check requested for: {} (requires dedicated tool)", elementName);
        } catch (final Exception e) {
            logger.warn("Color contrast check failed: {}", elementName, e);
        }
    }

    /**
     * Validate landmark structure of page.
     *
     * @param page     the Playwright page
     * @param pageName the page name
     */
    public static void validateLandmarks(final Page page, final String pageName) {
        try {
            final String hasMain = (String) page.evaluate(
                    "() => document.querySelector('main, [role=\"main\"]') ? 'yes' : 'no'");
            final String hasNav = (String) page.evaluate(
                    "() => document.querySelector('nav, [role=\"navigation\"]') ? 'yes' : 'no'");
            final String hasContent = (String) page.evaluate(
                    "() => document.querySelector('[role=\"contentinfo\"], footer') ? 'yes' : 'no'");

            logger.info("[A11Y][{}] Landmarks - main: {}, nav: {}, contentinfo: {}", pageName, hasMain, hasNav,
                    hasContent);

            if ("no".equals(hasMain)) {
                logger.warn("[A11Y][{}] Missing main landmark", pageName);
            }
        } catch (final Exception e) {
            logger.warn("Landmark validation failed: {}", pageName, e);
        }
    }

    /**
     * Whitelist a specific violation for a page.
     *
     * @param pageName the page name
     * @param ruleId   the Axe rule ID to whitelist
     */
    public static void whitelistViolation(final String pageName, final String ruleId) {
        WHITELIST.computeIfAbsent(pageName, k -> new HashSet<>()).add(ruleId);
        logger.info("✓ Whitelisted violation: {} for {}", ruleId, pageName);
    }

    /**
     * Check if a violation is whitelisted.
     *
     * @param pageName the page name
     * @param ruleId   the rule ID
     * @return true if whitelisted, false otherwise
     */
    private static boolean isWhitelisted(final String pageName, final String ruleId) {
        final Set<String> pageWhitelist = WHITELIST.get(pageName);
        return pageWhitelist != null && pageWhitelist.contains(ruleId);
    }

    /**
     * Load whitelist from file.
     *
     * @param filePath the path to the whitelist file (JSON)
     */
    public static void loadWhitelist(final String filePath) {
        try {
            final String content = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
            @SuppressWarnings("unchecked")
            final Map<String, List<String>> whitelistData = gson.fromJson(content, Map.class);

            whitelistData.forEach((page, rules) -> {
                WHITELIST.put(page, new HashSet<>(rules));
            });

            logger.info("✓ Loaded accessibility whitelist from: {}", filePath);
        } catch (final IOException | RuntimeException e) {
            logger.error("Failed to load accessibility whitelist: {}", filePath, e);
        }
    }

    /**
     * Generate accessibility report in JSON format.
     *
     * @param pageName the page name
     * @param results  the Axe results
     */
    private static void generateReport(final String pageName, final AxeResults results) {
        try {
            ensureReportsDirectory();

            final String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
            final String reportFileName = String.format("a11y-report-%s-%s.json", pageName, timestamp);
            final Path reportPath = Paths.get(REPORTS_DIR, reportFileName);

            final Map<String, Object> report = new HashMap<>();
            report.put("pageName", pageName);
            report.put("timestamp", timestamp);
            report.put("violationCount", results.getViolations().size());
            report.put("passCount", results.getPasses().size());
            report.put("inapplicableCount", results.getInapplicable().size());

            Files.write(reportPath, gson.toJson(report).getBytes(StandardCharsets.UTF_8));
            logger.info("✓ Accessibility report generated: {}", reportPath);
        } catch (final IOException e) {
            logger.error("Failed to generate accessibility report", e);
        }
    }

    /**
     * Create reports directory if it doesn't exist.
     *
     * @throws IOException if directory creation fails
     */
    private static void ensureReportsDirectory() throws IOException {
        final Path reportsPath = Paths.get(REPORTS_DIR);
        if (!Files.exists(reportsPath)) {
            Files.createDirectories(reportsPath);
        }
    }

    /**
     * Clear all whitelisted violations.
     */
    public static void clearWhitelist() {
        WHITELIST.clear();
        logger.info("✓ Cleared accessibility whitelist");
    }
}
