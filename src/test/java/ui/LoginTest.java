package ui;

import core.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.AccessibilityUtil;
import utils.AssertionHelper;
import utils.ConfigReader;
import utils.MockHelper;
import utils.WaitHelper;

/**
 * Test suite for login functionality.
 * Demonstrates best practices: clear naming, proper logging, reusable helpers.
 */
@DisplayName("Login Feature Tests")
class LoginTest extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(LoginTest.class);
    private static final String TEST_NAME = "LoginTest";

    @BeforeEach
    void navigateToLoginPage() {
        logger.info("Navigating to login page");
        final String baseUrl = ConfigReader.getBaseUrl();
        getPage().navigate(baseUrl);
        logger.debug("✓ Navigated to: {}", baseUrl);
    }

    @Test
    @DisplayName("User should be able to login with valid credentials")
    void loginWithValidCredentials_shouldNavigateToHomepage() {
        logger.info("Test: loginWithValidCredentials_shouldNavigateToHomepage");

        try {
            // Setup mock for login API
            final String mockResponse = "{\"success\": true, \"token\": \"abc123\"}";
            MockHelper.mockRoute(getPage(), "*/api/login", mockResponse, 200);

            // Enter credentials
            final String validEmail = "user@example.com";
            final String validPassword = "SecurePassword123!";

            logger.debug("Filling login form with email: {}", validEmail);
            getPage().fill("#email", validEmail);
            getPage().fill("#password", validPassword);

            // Click login button
            getPage().click("#loginButton");

            // Verify navigation
            WaitHelper.waitFor(
                    () -> getPage().url().contains("/home"),
                    "Navigation to home page");

            AssertionHelper.assertPageUrl(getPage(), "/home", TEST_NAME);
            logger.info("✓ Test passed: User logged in successfully");

        } catch (final Exception e) {
            logger.error("Test failed: {}", TEST_NAME, e);
            throw e;
        }
    }

    @Test
    @DisplayName("User should see validation error with empty email")
    void loginWithEmptyEmail_shouldShowValidationError() {
        logger.info("Test: loginWithEmptyEmail_shouldShowValidationError");

        try {
            // Fill only password
            getPage().fill("#password", "SomePassword123!");
            getPage().click("#loginButton");

            // Verify error message appears
            WaitHelper.waitForElement(getPage().locator(".error-message"), "Error message");
            AssertionHelper.assertElementVisible(
                    getPage().locator(".error-message"),
                    "Validation error message",
                    TEST_NAME);

            logger.info("✓ Test passed: Validation error displayed");

        } catch (final Exception e) {
            logger.error("Test failed: {}", TEST_NAME, e);
            throw e;
        }
    }

    @Test
    @DisplayName("Login page should meet accessibility standards")
    void loginPage_shouldMeetAccessibilityStandards() {
        logger.info("Test: loginPage_shouldMeetAccessibilityStandards");

        try {
            // Run accessibility scan
            AccessibilityUtil.scan(getPage(), "LoginPage");

            // Check keyboard navigation
            AccessibilityUtil.checkKeyboardNavigation(getPage(), "LoginPage");

            // Validate ARIA attributes on form fields
            AccessibilityUtil.validateAriaAttributes(
                    getPage().locator("#email"),
                    "Email input");
            AccessibilityUtil.validateAriaAttributes(
                    getPage().locator("#password"),
                    "Password input");

            logger.info("✓ Test passed: Accessibility standards verified");

        } catch (final Exception e) {
            logger.error("Test failed: {}", TEST_NAME, e);
            throw e;
        }
    }

    @Test
    @DisplayName("Login should handle API timeout gracefully")
    void loginWithApiTimeout_shouldShowTimeoutError() {
        logger.info("Test: loginWithApiTimeout_shouldShowTimeoutError");

        try {
            // Abort login API to simulate timeout
            MockHelper.mockRouteAbort(getPage(), "*/api/login");

            getPage().fill("#email", "user@example.com");
            getPage().fill("#password", "password123");
            getPage().click("#loginButton");

            // Should show error
            WaitHelper.waitForElement(
                    getPage().locator(".error-message"),
                    "Timeout error message");

            AssertionHelper.assertElementText(
                    getPage().locator(".error-message"),
                    "Network error",
                    TEST_NAME);

            logger.info("✓ Test passed: Timeout handled gracefully");

        } catch (final Exception e) {
            logger.error("Test failed: {}", TEST_NAME, e);
            throw e;
        }
    }
}
