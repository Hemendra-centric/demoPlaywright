package utils;

import com.microsoft.playwright.Page;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages test artifacts including screenshots, videos, and traces.
 * Provides centralized artifact storage and cleanup functionality.
 */
public final class ArtifactManager {

    private static final Logger logger = LoggerFactory.getLogger(ArtifactManager.class);
    private static final String ARTIFACTS_DIR = "target/test-artifacts";
    private static final String SCREENSHOTS_DIR = ARTIFACTS_DIR + "/screenshots";
    private static final String VIDEOS_DIR = ARTIFACTS_DIR + "/videos";
    private static final String TRACES_DIR = ARTIFACTS_DIR + "/traces";
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss-SSS");

    private ArtifactManager() {
        // Utility class - no instantiation
    }

    /**
     * Initialize artifact directories.
     */
    public static void initialize() {
        try {
            createDirectory(ARTIFACTS_DIR);
            createDirectory(SCREENSHOTS_DIR);
            createDirectory(VIDEOS_DIR);
            createDirectory(TRACES_DIR);
            logger.info("✓ Artifact manager initialized");
        } catch (final Exception e) {
            logger.error("Failed to initialize artifact directories", e);
        }
    }

    /**
     * Take a screenshot and save it.
     *
     * @param page     the Playwright page
     * @param testName the test name
     * @return the path to the screenshot file
     */
    public static String takeScreenshot(final Page page, final String testName) {
        return takeScreenshot(page, testName, false);
    }

    /**
     * Take a full-page screenshot.
     *
     * @param page     the Playwright page
     * @param testName the test name
     * @param fullPage true to capture full page, false for visible area
     * @return the path to the screenshot file
     */
    public static String takeScreenshot(final Page page, final String testName, final boolean fullPage) {
        try {
            final String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
            final String fileName = String.format("%s-%s.png", testName, timestamp);
            final Path filePath = Paths.get(SCREENSHOTS_DIR, fileName);

            page.screenshot(new Page.ScreenshotOptions().setFullPage(fullPage).setPath(filePath));
            logger.info("✓ Screenshot captured: {}", filePath);
            return filePath.toString();
        } catch (final Exception e) {
            logger.error("Failed to capture screenshot for test: {}", testName, e);
            return null;
        }
    }

    /**
     * Get artifacts directory path.
     *
     * @return the artifacts directory path
     */
    public static String getArtifactsDir() {
        return ARTIFACTS_DIR;
    }

    /**
     * Get screenshots directory path.
     *
     * @return the screenshots directory path
     */
    public static String getScreenshotsDir() {
        return SCREENSHOTS_DIR;
    }

    /**
     * Get videos directory path.
     *
     * @return the videos directory path
     */
    public static String getVideosDir() {
        return VIDEOS_DIR;
    }

    /**
     * Get traces directory path.
     *
     * @return the traces directory path
     */
    public static String getTracesDir() {
        return TRACES_DIR;
    }

    /**
     * Create a directory if it doesn't exist.
     *
     * @param dirPath the directory path
     * @throws Exception if directory creation fails
     */
    private static void createDirectory(final String dirPath) throws Exception {
        final Path path = Paths.get(dirPath);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
            logger.debug("✓ Created directory: {}", dirPath);
        }
    }
}
