package utils;

import com.deque.html.axecore.playwright.AxeBuilder;
import com.deque.html.axecore.results.AxeResults;
import com.deque.html.axecore.results.Rule;
import com.microsoft.playwright.Page;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AccessibilityUtil {

    private static final Logger logger = LoggerFactory.getLogger(AccessibilityUtil.class);
    private static final Set<String> FAILING_IMPACTS = Set.of("critical", "serious");

    private AccessibilityUtil() {
    }

    public static void scan(final Page page, final String pageName) {

        final AxeResults results = new AxeBuilder(page)
                .withTags(List.of("wcag2a", "wcag2aa"))
                .analyze();

        final List<Rule> violations = results.getViolations();

        boolean hasBlockingIssues = false;

        for (final Rule rule : violations) {
            final String impact = rule.getImpact();

            logger.info("[A11Y][{}] {} | Impact: {}", pageName, rule.getId(), impact);

            if (impact != null && FAILING_IMPACTS.contains(impact)) {
                hasBlockingIssues = true;

                rule.getNodes().forEach(node -> logger.warn("  ↳ {}", node.getHtml()));
            }
        }

        if (hasBlockingIssues) {
            throw new AssertionError(
                    "Accessibility blocking issues found on " + pageName);
        }

        logger.info("✅ Accessibility scan passed: {}", pageName);
    }
}
