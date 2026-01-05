package cucumber;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import io.cucumber.java.Before;
import io.cucumber.java.After;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import core.BrowserManager;
import utils.ArtifactManager;
import utils.ConfigReader;

/**
 * Cucumber hooks for browser lifecycle management.
 * Runs before and after each scenario to set up and tear down browser context.
 */
public class BrowserHooks {

    private static final Logger logger = LoggerFactory.getLogger(BrowserHooks.class);
    private static BrowserManager browserManager;

    @Before
    public void beforeScenario(final io.cucumber.java.Scenario scenario) {
        logger.info("Starting scenario: {}", scenario.getName());

        // Initialize BrowserManager once per feature
        if (browserManager == null) {
            browserManager = new BrowserManager();
            ArtifactManager.initialize();
        }

        final Browser browser = browserManager.getBrowser();
        final BrowserContext context = browser.newContext();
        final Page page = context.newPage();
        page.setDefaultTimeout(ConfigReader.getInt("timeout", 30000));

        ScenarioContext.setContext(context);
        ScenarioContext.setPage(page);

        logger.debug("✓ Browser context initialized for scenario: {}", scenario.getName());
    }

    @After
    public void afterScenario(final io.cucumber.java.Scenario scenario) {
        logger.info("Finishing scenario: {} (status: {})", scenario.getName(), scenario.getStatus());

        final Page page = ScenarioContext.getPage();
        final BrowserContext context = ScenarioContext.getContext();

        // Capture screenshot on failure
        if (scenario.isFailed() && page != null) {
            try {
                final String screenshotPath = ArtifactManager.takeScreenshot(page, scenario.getName() + "_FAILED",
                        true);
                logger.info("✓ Screenshot captured on failure: {}", screenshotPath);
            } catch (final Exception e) {
                logger.warn("Failed to capture screenshot for scenario: {}", scenario.getName(), e);
            }
        }

        if (context != null) {
            context.close();
            logger.debug("✓ Browser context closed");
        }

        ScenarioContext.cleanup();
    }
}
