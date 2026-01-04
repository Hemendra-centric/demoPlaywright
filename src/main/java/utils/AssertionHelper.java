package utils;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Custom assertion helper providing fluent assertions for Playwright tests.
 * Offers consistent assertion patterns and better error messages.
 */
public final class AssertionHelper {

    private static final Logger logger = LoggerFactory.getLogger(AssertionHelper.class);

    private AssertionHelper() {
        // Utility class - no instantiation
    }

    /**
     * Assert that a page is on the expected URL.
     *
     * @param page        the Playwright page
     * @param expectedUrl the expected URL pattern
     * @param testName    the test name for logging
     */
    public static void assertPageUrl(final Page page, final String expectedUrl, final String testName) {
        final String currentUrl = page.url();
        if (!currentUrl.contains(expectedUrl)) {
            final String message = String.format(
                    "[%s] Expected URL to contain '%s' but got '%s'",
                    testName,
                    expectedUrl,
                    currentUrl);
            logger.error(message);
            throw new AssertionError(message);
        }
        logger.debug("[{}] ✓ Page URL assertion passed: {}", testName, currentUrl);
    }

    /**
     * Assert that a locator is visible on the page.
     *
     * @param locator     the locator to check
     * @param description the description of what should be visible
     * @param testName    the test name for logging
     */
    public static void assertElementVisible(final Locator locator, final String description, final String testName) {
        try {
            if (!locator.isVisible()) {
                final String message = String.format("[%s] Expected element to be visible: %s", testName, description);
                logger.error(message);
                throw new AssertionError(message);
            }
            logger.debug("[{}] ✓ Element is visible: {}", testName, description);
        } catch (final Exception e) {
            final String message = String.format("[%s] Element visibility check failed: %s", testName, description);
            logger.error(message, e);
            throw new AssertionError(message, e);
        }
    }

    /**
     * Assert that a locator is hidden on the page.
     *
     * @param locator     the locator to check
     * @param description the description of what should be hidden
     * @param testName    the test name for logging
     */
    public static void assertElementHidden(final Locator locator, final String description, final String testName) {
        try {
            if (locator.isVisible()) {
                final String message = String.format("[%s] Expected element to be hidden: %s", testName, description);
                logger.error(message);
                throw new AssertionError(message);
            }
            logger.debug("[{}] ✓ Element is hidden: {}", testName, description);
        } catch (final Exception e) {
            final String message = String.format("[%s] Element hidden check failed: %s", testName, description);
            logger.error(message, e);
            throw new AssertionError(message, e);
        }
    }

    /**
     * Assert that a locator has expected text content.
     *
     * @param locator      the locator to check
     * @param expectedText the expected text content
     * @param testName     the test name for logging
     */
    public static void assertElementText(final Locator locator, final String expectedText, final String testName) {
        try {
            final String actualText = locator.textContent();
            if (actualText == null || !actualText.contains(expectedText)) {
                final String message = String.format(
                        "[%s] Expected text to contain '%s' but got '%s'",
                        testName,
                        expectedText,
                        actualText);
                logger.error(message);
                throw new AssertionError(message);
            }
            logger.debug("[{}] ✓ Element text assertion passed: {}", testName, expectedText);
        } catch (final Exception e) {
            final String message = String.format("[%s] Failed to assert element text", testName);
            logger.error(message, e);
            throw new AssertionError(message, e);
        }
    }

    /**
     * Assert that a locator is enabled (not disabled).
     *
     * @param locator     the locator to check
     * @param description the description of the element
     * @param testName    the test name for logging
     */
    public static void assertElementEnabled(final Locator locator, final String description, final String testName) {
        try {
            final boolean isEnabled = locator.isEnabled();
            if (!isEnabled) {
                final String message = String.format("[%s] Expected element to be enabled: %s", testName, description);
                logger.error(message);
                throw new AssertionError(message);
            }
            logger.debug("[{}] ✓ Element is enabled: {}", testName, description);
        } catch (final Exception e) {
            final String message = String.format("[%s] Failed to assert element enabled state", testName);
            logger.error(message, e);
            throw new AssertionError(message, e);
        }
    }

    /**
     * Assert that a locator has expected attribute value.
     *
     * @param locator       the locator to check
     * @param attributeName the name of the attribute
     * @param expectedValue the expected attribute value
     * @param testName      the test name for logging
     */
    public static void assertElementAttribute(
            final Locator locator,
            final String attributeName,
            final String expectedValue,
            final String testName) {
        try {
            final String actualValue = locator.getAttribute(attributeName);
            if (!expectedValue.equals(actualValue)) {
                final String message = String.format(
                        "[%s] Expected attribute '%s' to be '%s' but got '%s'",
                        testName,
                        attributeName,
                        expectedValue,
                        actualValue);
                logger.error(message);
                throw new AssertionError(message);
            }
            logger.debug("[{}] ✓ Attribute assertion passed: {}={}", testName, attributeName, expectedValue);
        } catch (final Exception e) {
            final String message = String.format(
                    "[%s] Failed to assert element attribute: %s",
                    testName,
                    attributeName);
            logger.error(message, e);
            throw new AssertionError(message, e);
        }
    }

    /**
     * Assert generic condition with custom message.
     *
     * @param condition the condition to check
     * @param message   the error message if condition is false
     */
    public static void assertTrue(final boolean condition, final String message) {
        if (!condition) {
            logger.error(message);
            throw new AssertionError(message);
        }
        logger.debug("✓ Assertion passed");
    }

    /**
     * Assert that a value is not null.
     *
     * @param value     the value to check
     * @param fieldName the name of the field
     * @param testName  the test name for logging
     */
    public static void assertNotNull(final Object value, final String fieldName, final String testName) {
        if (value == null) {
            final String message = String.format("[%s] %s should not be null", testName, fieldName);
            logger.error(message);
            throw new AssertionError(message);
        }
        logger.debug("[{}] ✓ {} is not null", testName, fieldName);
    }
}
