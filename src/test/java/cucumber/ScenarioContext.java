package cucumber;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;

/**
 * Shared context to pass Page and BrowserContext between Cucumber hooks and
 * step definitions.
 * Uses thread-local storage to ensure isolation between parallel test threads.
 */
public class ScenarioContext {

    private static final ThreadLocal<Page> PAGE = new ThreadLocal<>();
    private static final ThreadLocal<BrowserContext> CONTEXT = new ThreadLocal<>();

    public static void setPage(final Page page) {
        PAGE.set(page);
    }

    public static Page getPage() {
        return PAGE.get();
    }

    public static void setContext(final BrowserContext context) {
        CONTEXT.set(context);
    }

    public static BrowserContext getContext() {
        return CONTEXT.get();
    }

    public static void cleanup() {
        PAGE.remove();
        CONTEXT.remove();
    }
}
