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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Cucumber hooks for browser lifecycle management.
 * Runs before and after each scenario to set up and tear down browser context.
 */
public class BrowserHooks {

    private static final Logger logger = LoggerFactory.getLogger(BrowserHooks.class);
    private static BrowserManager browserManager;

    // Store page and context as instance variables to avoid ThreadLocal issues
    private Page currentPage;
    private BrowserContext currentContext;

    @Before
    public void beforeScenario(final io.cucumber.java.Scenario scenario) {
        logger.info("Starting scenario: {}", scenario.getName());

        // Initialize BrowserManager once per feature
        if (browserManager == null) {
            browserManager = new BrowserManager();
            ArtifactManager.initialize();
        }

        final Browser browser = browserManager.getBrowser();

        // Configure video recording if enabled
        final boolean recordVideo = ConfigReader.getBoolean("video.record");
        final boolean recordAlways = ConfigReader.getBoolean("video.record.always");

        final Browser.NewContextOptions contextOptions = new Browser.NewContextOptions();
        if (recordVideo || recordAlways) {
            contextOptions.setRecordVideoDir(Paths.get(ArtifactManager.getVideosDir()));
        }

        currentContext = browser.newContext(contextOptions);
        currentPage = currentContext.newPage();
        currentPage.setDefaultTimeout(ConfigReader.getInt("timeout", 30000));

        // Also store in ScenarioContext for steps that need it
        ScenarioContext.setContext(currentContext);
        ScenarioContext.setPage(currentPage);

        logger.debug("✓ Browser context initialized for scenario: {}", scenario.getName());
        logger.info("DEBUG BEFORE - Page set: {}, Page object: {}", currentPage != null, currentPage);
        if (recordVideo || recordAlways) {
            logger.info("✓ Video recording enabled for scenario: {}", scenario.getName());
        }
    }

    @After(order = 1) // Run first (before APIHooks which is order 10)
    public void afterScenario(final io.cucumber.java.Scenario scenario) {
        logger.info("Finishing scenario: {} (status: {})", scenario.getName(), scenario.getStatus());

        // Use instance variables instead of ThreadLocal to avoid issues
        logger.info("DEBUG - currentPage is null: {}, currentContext is null: {}", currentPage == null,
                currentContext == null);
        logger.info("DEBUG - Scenario failed: {}", scenario.isFailed());

        // Capture screenshot on failure and embed in report
        if (scenario.isFailed() && currentPage != null) {
            logger.info("Capturing screenshot for failed scenario: {}", scenario.getName());
            try {
                final String screenshotPath = ArtifactManager.takeScreenshot(currentPage,
                        scenario.getName() + "_FAILED",
                        true);

                logger.info("DEBUG - Screenshot path returned: {}", screenshotPath);

                if (screenshotPath != null) {
                    try {
                        // Embed screenshot in Cucumber report
                        final byte[] screenshotData = Files.readAllBytes(Paths.get(screenshotPath));
                        scenario.attach(screenshotData, "image/png", "Failed Screenshot - " + scenario.getName());
                        logger.info("✓ Screenshot captured and embedded in report: {}", screenshotPath);
                    } catch (final Exception attachException) {
                        logger.error("Failed to read screenshot file: {}", screenshotPath, attachException);
                    }
                } else {
                    logger.warn("Screenshot path is null - capture may have failed");
                }
            } catch (final Exception e) {
                logger.warn("Failed to capture and embed screenshot for scenario: {}", scenario.getName(), e);
            }
        } else {
            if (scenario.isFailed()) {
                logger.warn("Cannot capture screenshot - page is null for failed scenario");
            }
        }

        if (currentContext != null) {
            currentContext.close();
            logger.debug("✓ Browser context closed");
        }

        // Don't cleanup here - let the cleanup hook do it
        // This allows APIHooks to access the data before cleanup
        logger.debug("✓ @After(order=1) BrowserHooks completed");
    }
}
