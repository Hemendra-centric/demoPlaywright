package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.CommonFunction;
import utils.ConfigReader;

/**
 * Page Object for Login page.
 * Encapsulates all interactions and assertions for the login page.
 */
public class LoginPage {

    private static final Logger logger = LoggerFactory.getLogger(LoginPage.class);
    private final Page page;

    // Locators
    private final String EMAIL_INPUT = "#email";
    private final String PASSWORD_INPUT = "#password";
    private final String LOGIN_BUTTON = "#loginButton";
    private final String ERROR_MESSAGE = ".error-message";
    private final String SUCCESS_MESSAGE = ".success-message";

    public LoginPage(final Page page) {
        this.page = page;
    }

    /**
     * Navigate to the login page.
     */
    public void open() {
        logger.info("Opening login page");
        page.navigate(ConfigReader.getBaseUrl() + "/login.html");
        logger.debug("✓ Login page opened");
    }

    /**
     * Enter email address in the email field.
     *
     * @param email the email to enter
     */
    public void enterEmail(final String email) {
        logger.debug("Entering email: {}", email);
        final Locator emailField = page.locator(EMAIL_INPUT);
        CommonFunction.fill(emailField, email, "Email input field");
    }

    /**
     * Enter password in the password field.
     *
     * @param password the password to enter
     */
    public void enterPassword(final String password) {
        logger.debug("Entering password");
        final Locator passwordField = page.locator(PASSWORD_INPUT);
        CommonFunction.fill(passwordField, password, "Password input field");
    }

    /**
     * Click the login button.
     */
    public void clickLoginButton() {
        logger.info("Clicking login button");
        final Locator button = page.locator(LOGIN_BUTTON);
        CommonFunction.click(button, "Login button");
    }

    /**
     * Login with given credentials.
     *
     * @param email    the email address
     * @param password the password
     */
    public void login(final String email, final String password) {
        logger.info("Logging in with email: {}", email);
        enterEmail(email);
        enterPassword(password);
        clickLoginButton();
    }

    /**
     * Verify error message is displayed.
     *
     * @param expectedMessage the expected error message
     */
    public void verifyErrorMessage(final String expectedMessage) {
        logger.info("Verifying error message: {}", expectedMessage);
        final Locator errorMsg = page.locator(ERROR_MESSAGE);
        CommonFunction.assertElementVisible(errorMsg, "Error message");
        CommonFunction.assertElementText(errorMsg, expectedMessage);
    }

    /**
     * Verify success message is displayed.
     */
    public void verifySuccessMessage() {
        logger.info("Verifying success message");
        final Locator successMsg = page.locator(SUCCESS_MESSAGE);
        CommonFunction.assertElementVisible(successMsg, "Success message");
    }

    /**
     * Get current page title.
     *
     * @return the page title
     */
    public String getPageTitle() {
        return page.title();
    }
}
