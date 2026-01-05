package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.CommonFunction;

/**
 * Page Object for Home page.
 * Encapsulates all interactions and assertions for the home page.
 */
public class HomePage {

    private static final Logger logger = LoggerFactory.getLogger(HomePage.class);
    private final Page page;

    // Locators
    private final String WELCOME_MESSAGE = ".username";
    private final String USER_NAME = ".username";
    private final String LOGOUT_BUTTON = "#logoutButton";

    public HomePage(final Page page) {
        this.page = page;
    }

    /**
     * Verify user is on the home page by checking the URL.
     */
    public void verifyOnHomePage() {
        logger.info("Verifying on home page");
        // Check if current URL contains "home"
        final String url = page.url();
        if (!url.contains("home")) {
            throw new AssertionError("Not on home page. Current URL: " + url);
        }
        logger.debug("✓ Verified on home page");
    }

    /**
     * Get the username displayed on home page.
     *
     * @return the username
     */
    public String getUserName() {
        logger.debug("Getting user name from home page");
        final Locator userNameElement = page.locator(USER_NAME);
        CommonFunction.waitForElement(userNameElement, "User name element");
        return CommonFunction.getText(userNameElement);
    }

    /**
     * Click logout button.
     */
    public void logout() {
        logger.info("Clicking logout button");
        final Locator logoutBtn = page.locator(LOGOUT_BUTTON);
        CommonFunction.click(logoutBtn, "Logout button");
    }

    /**
     * Verify page URL contains expected path.
     *
     * @param expectedPath the expected path
     */
    public void verifyPageUrl(final String expectedPath) {
        logger.info("Verifying page URL contains: {}", expectedPath);
        CommonFunction.assertPageUrl(page, expectedPath);
    }
}
