package core;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ArtifactManager;
import utils.ConfigReader;

/**
 * JUnit 5 extension managing browser lifecycle with support for
 * artifact management and screenshots on failure.
 */
public class BrowserExtension implements BeforeAllCallback, AfterAllCallback, BeforeEachCallback, AfterEachCallback {

    private static final Logger logger = LoggerFactory.getLogger(BrowserExtension.class);
    private static BrowserManager browserManager;

    @Override
    public void beforeAll(final ExtensionContext extensionContext) throws Exception {
        logger.info("Initializing browser for test class: {}", extensionContext.getDisplayName());

        if (browserManager == null) {
            browserManager = new BrowserManager();
        }

        ArtifactManager.initialize();
    }

    @Override
    public void afterAll(final ExtensionContext extensionContext) throws Exception {
        logger.info("Closing browser for test class: {}", extensionContext.getDisplayName());

        if (browserManager != null) {
            browserManager.closeAll();
            browserManager = null;
        }
    }

    @Override
    public void beforeEach(final ExtensionContext extensionContext) throws Exception {
        final String testName = extensionContext.getDisplayName();
        logger.info("Setting up browser context for test: {}", testName);

        final Object testInstance = extensionContext.getRequiredTestInstance();
        if (!(testInstance instanceof BaseTest)) {
            return;
        }

        final BaseTest test = (BaseTest) testInstance;
        final Browser browser = browserManager.getBrowser();

        final BrowserContext context = browser.newContext();
        final Page page = context.newPage();
        page.setDefaultTimeout(ConfigReader.getInt("timeout", 30000));

        test.setContext(context);
        test.setPage(page);

        logger.info("✓ Browser context initialized for test: {}", testName);
    }

    @Override
    public void afterEach(final ExtensionContext extensionContext) throws Exception {
        final String testName = extensionContext.getDisplayName();
        final Throwable testException = extensionContext.getExecutionException().orElse(null);
        final boolean testFailed = testException != null;

        logger.info("Tearing down browser context for test: {} (status: {})", testName,
                testFailed ? "FAILED" : "PASSED");

        final Object testInstance = extensionContext.getRequiredTestInstance();
        if (!(testInstance instanceof BaseTest)) {
            return;
        }

        final BaseTest test = (BaseTest) testInstance;
        final BrowserContext context = test.getContext();
        final Page page = test.getPage();

        // Capture screenshot on failure
        if (testFailed && page != null) {
            try {
                final String screenshotPath = ArtifactManager.takeScreenshot(page, testName + "_FAILED", true);
                logger.info("✓ Screenshot captured on failure: {}", screenshotPath);
            } catch (final Exception e) {
                logger.warn("Failed to capture screenshot on failure: {}", testName, e);
            }
        }

        if (context != null) {
            context.close();
            logger.debug("✓ Browser context closed");
        }

        test.setContext(null);
        test.setPage(null);
    }
}
