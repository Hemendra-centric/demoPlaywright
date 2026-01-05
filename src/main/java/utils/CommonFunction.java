package utils;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Consolidated utility class containing all common functions for test
 * automation.
 * Includes: wait operations, actions (click, fill, etc.), assertions, and
 * retries.
 */
public final class CommonFunction {

    private static final Logger logger = LoggerFactory.getLogger(CommonFunction.class);
    private static final int DEFAULT_TIMEOUT_MS = 5000;
    private static final int DEFAULT_POLL_INTERVAL_MS = 500;

    private CommonFunction() {
        // Utility class - no instantiation
    }

    // ============= WAIT FUNCTIONS =============

    /**
     * Wait for an element to be visible.
     *
     * @param locator     the Playwright locator
     * @param description description of the element
     * @throws TimeoutException if element is not visible within timeout
     */
    public static void waitForElement(final Locator locator, final String description) {
        waitForElement(locator, description, DEFAULT_TIMEOUT_MS);
    }

    /**
     * Wait for an element to be visible with custom timeout.
     *
     * @param locator     the Playwright locator
     * @param description description of the element
     * @param timeoutMs   timeout in milliseconds
     * @throws TimeoutException if element is not visible within timeout
     */
    public static void waitForElement(final Locator locator, final String description, final int timeoutMs) {
        waitFor(() -> locator.isVisible(), "Element visible: " + description, timeoutMs);
    }

    /**
     * Wait for a condition with default timeout.
     *
     * @param condition   supplier that returns true when condition is met
     * @param description description of what is being waited for
     * @throws TimeoutException if condition is not met within timeout
     */
    public static void waitFor(final Supplier<Boolean> condition, final String description) {
        waitFor(condition, description, DEFAULT_TIMEOUT_MS);
    }

    /**
     * Wait for a condition with custom timeout.
     *
     * @param condition   supplier that returns true when condition is met
     * @param description description of what is being waited for
     * @param timeoutMs   timeout in milliseconds
     * @throws TimeoutException if condition is not met within timeout
     */
    public static void waitFor(final Supplier<Boolean> condition, final String description, final int timeoutMs) {
        final long endTime = System.currentTimeMillis() + timeoutMs;

        while (System.currentTimeMillis() < endTime) {
            try {
                if (condition.get()) {
                    logger.debug("✓ Wait condition met: {}", description);
                    return;
                }
            } catch (final Exception e) {
                logger.trace("Condition not yet met: {}", description, e);
            }

            try {
                Thread.sleep(DEFAULT_POLL_INTERVAL_MS);
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Wait interrupted for: " + description, e);
            }
        }

        final String message = String.format("Timeout waiting for: %s (timeout: %dms)", description, timeoutMs);
        logger.error(message);
        throw new TimeoutException(message);
    }

    // ============= ACTION FUNCTIONS =============

    /**
     * Click on an element after waiting for it to be visible.
     *
     * @param locator     the element locator
     * @param description description of the element
     */
    public static void click(final Locator locator, final String description) {
        waitForElement(locator, description);
        logger.debug("Clicking: {}", description);
        locator.click();
    }

    /**
     * Fill text in an input field after waiting for it.
     *
     * @param locator     the input field locator
     * @param text        the text to fill
     * @param description description of the field
     */
    public static void fill(final Locator locator, final String text, final String description) {
        waitForElement(locator, description);
        logger.debug("Filling: {} with text", description);
        locator.fill(text);
    }

    /**
     * Clear an input field and fill with text.
     *
     * @param locator     the input field locator
     * @param text        the text to fill
     * @param description description of the field
     */
    public static void clearAndFill(final Locator locator, final String text, final String description) {
        waitForElement(locator, description);
        logger.debug("Clearing and filling: {}", description);
        locator.clear();
        locator.fill(text);
    }

    /**
     * Type text character by character in an input field.
     *
     * @param locator     the input field locator
     * @param text        the text to type
     * @param description description of the field
     */
    public static void type(final Locator locator, final String text, final String description) {
        waitForElement(locator, description);
        logger.debug("Typing in: {}", description);
        locator.type(text);
    }

    /**
     * Press a key on the page.
     *
     * @param page the Playwright page
     * @param key  the key to press (e.g., "Enter", "Tab", "Escape")
     */
    public static void pressKey(final Page page, final String key) {
        logger.debug("Pressing key: {}", key);
        page.keyboard().press(key);
    }

    /**
     * Select an option from a dropdown by value.
     *
     * @param locator     the select element locator
     * @param value       the value to select
     * @param description description of the dropdown
     */
    public static void selectByValue(final Locator locator, final String value, final String description) {
        waitForElement(locator, description);
        logger.debug("Selecting value in {}: {}", description, value);
        locator.selectOption(value);
    }

    /**
     * Select an option from a dropdown by label.
     *
     * @param locator     the select element locator
     * @param label       the label to select
     * @param description description of the dropdown
     */
    public static void selectByLabel(final Locator locator, final String label, final String description) {
        waitForElement(locator, description);
        logger.debug("Selecting option in {}: {}", description, label);
        locator.selectOption(label);
    }

    /**
     * Check a checkbox.
     *
     * @param locator     the checkbox locator
     * @param description description of the checkbox
     */
    public static void check(final Locator locator, final String description) {
        waitForElement(locator, description);
        logger.debug("Checking: {}", description);
        locator.check();
    }

    /**
     * Uncheck a checkbox.
     *
     * @param locator     the checkbox locator
     * @param description description of the checkbox
     */
    public static void uncheck(final Locator locator, final String description) {
        waitForElement(locator, description);
        logger.debug("Unchecking: {}", description);
        locator.uncheck();
    }

    /**
     * Hover over an element.
     *
     * @param locator     the element locator
     * @param description description of the element
     */
    public static void hover(final Locator locator, final String description) {
        waitForElement(locator, description);
        logger.debug("Hovering over: {}", description);
        locator.hover();
    }

    /**
     * Scroll element into view.
     *
     * @param locator     the element locator
     * @param description description of the element
     */
    public static void scrollIntoView(final Locator locator, final String description) {
        logger.debug("Scrolling into view: {}", description);
        locator.scrollIntoViewIfNeeded();
    }

    // ============= ASSERTION FUNCTIONS =============

    /**
     * Assert that a page is on the expected URL.
     *
     * @param page        the Playwright page
     * @param expectedUrl the expected URL pattern
     */
    public static void assertPageUrl(final Page page, final String expectedUrl) {
        final String currentUrl = page.url();
        if (!currentUrl.contains(expectedUrl)) {
            final String message = String.format(
                    "Expected URL to contain '%s' but got '%s'",
                    expectedUrl,
                    currentUrl);
            logger.error(message);
            throw new AssertionError(message);
        }
        logger.debug("✓ Page URL assertion passed: {}", currentUrl);
    }

    /**
     * Assert that a locator is visible on the page.
     *
     * @param locator     the locator to check
     * @param description the description of the element
     */
    public static void assertElementVisible(final Locator locator, final String description) {
        try {
            if (!locator.isVisible()) {
                final String message = String.format("Expected element to be visible: %s", description);
                logger.error(message);
                throw new AssertionError(message);
            }
            logger.debug("✓ Element is visible: {}", description);
        } catch (final Exception e) {
            final String message = String.format("Element visibility check failed: %s", description);
            logger.error(message, e);
            throw new AssertionError(message, e);
        }
    }

    /**
     * Assert that a locator is hidden on the page.
     *
     * @param locator     the locator to check
     * @param description the description of the element
     */
    public static void assertElementHidden(final Locator locator, final String description) {
        try {
            if (locator.isVisible()) {
                final String message = String.format("Expected element to be hidden: %s", description);
                logger.error(message);
                throw new AssertionError(message);
            }
            logger.debug("✓ Element is hidden: {}", description);
        } catch (final Exception e) {
            final String message = String.format("Element hidden check failed: %s", description);
            logger.error(message, e);
            throw new AssertionError(message, e);
        }
    }

    /**
     * Assert that a locator has expected text content.
     *
     * @param locator      the locator to check
     * @param expectedText the expected text content
     */
    public static void assertElementText(final Locator locator, final String expectedText) {
        try {
            final String actualText = locator.textContent();
            if (actualText == null || !actualText.contains(expectedText)) {
                final String message = String.format(
                        "Expected text to contain '%s' but got '%s'",
                        expectedText,
                        actualText);
                logger.error(message);
                throw new AssertionError(message);
            }
            logger.debug("✓ Element text assertion passed: {}", expectedText);
        } catch (final Exception e) {
            final String message = "Failed to assert element text";
            logger.error(message, e);
            throw new AssertionError(message, e);
        }
    }

    /**
     * Assert that a locator is enabled (not disabled).
     *
     * @param locator     the locator to check
     * @param description the description of the element
     */
    public static void assertElementEnabled(final Locator locator, final String description) {
        try {
            final boolean isEnabled = locator.isEnabled();
            if (!isEnabled) {
                final String message = String.format("Expected element to be enabled: %s", description);
                logger.error(message);
                throw new AssertionError(message);
            }
            logger.debug("✓ Element is enabled: {}", description);
        } catch (final Exception e) {
            final String message = String.format("Failed to assert element enabled state: %s", description);
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
     */
    public static void assertElementAttribute(
            final Locator locator,
            final String attributeName,
            final String expectedValue) {
        try {
            final String actualValue = locator.getAttribute(attributeName);
            if (!expectedValue.equals(actualValue)) {
                final String message = String.format(
                        "Expected attribute '%s' to be '%s' but got '%s'",
                        attributeName,
                        expectedValue,
                        actualValue);
                logger.error(message);
                throw new AssertionError(message);
            }
            logger.debug("✓ Attribute assertion passed: {}={}", attributeName, expectedValue);
        } catch (final Exception e) {
            final String message = String.format("Failed to assert element attribute: %s", attributeName);
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
     */
    public static void assertNotNull(final Object value, final String fieldName) {
        if (value == null) {
            final String message = String.format("%s should not be null", fieldName);
            logger.error(message);
            throw new AssertionError(message);
        }
        logger.debug("✓ {} is not null", fieldName);
    }

    // ============= RETRY FUNCTIONS =============

    /**
     * Retry an action with exponential backoff.
     *
     * @param action      the action to retry
     * @param description description of the action
     * @param maxAttempts maximum number of attempts
     */
    public static void retry(final Runnable action, final String description, final int maxAttempts) {
        retry(() -> {
            action.run();
            return true;
        }, description, maxAttempts);
    }

    /**
     * Retry a supplier with exponential backoff.
     *
     * @param <T>         the return type
     * @param supplier    the supplier to retry
     * @param description description of the action
     * @param maxAttempts maximum number of attempts
     * @return the result from the supplier
     */
    public static <T> T retry(final Supplier<T> supplier, final String description, final int maxAttempts) {
        Exception lastException = null;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                logger.debug("Attempt {}/{}: {}", attempt, maxAttempts, description);
                final T result = supplier.get();
                if (attempt > 1) {
                    logger.info("✓ Retry successful for: {} (attempt {})", description, attempt);
                }
                return result;
            } catch (final Exception e) {
                lastException = e;
                logger.warn("Attempt {}/{} failed for: {}", attempt, maxAttempts, description, e);

                if (attempt < maxAttempts) {
                    final long waitMs = (long) Math.pow(2, attempt - 1) * 100;
                    try {
                        Thread.sleep(waitMs);
                    } catch (final InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Retry interrupted", ie);
                    }
                }
            }
        }

        final String message = String.format(
                "Failed to %s after %d attempts",
                description,
                maxAttempts);
        logger.error(message, lastException);
        throw new RuntimeException(message, lastException);
    }

    /**
     * Sleep for specified milliseconds.
     *
     * @param milliseconds the time to sleep
     */
    public static void sleep(final long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Sleep interrupted", e);
        }
    }

    // ============= HELPER FUNCTIONS =============

    /**
     * Get text content from an element.
     *
     * @param locator the element locator
     * @return the text content
     */
    public static String getText(final Locator locator) {
        return locator.textContent();
    }

    /**
     * Get attribute value from an element.
     *
     * @param locator       the element locator
     * @param attributeName the attribute name
     * @return the attribute value
     */
    public static String getAttribute(final Locator locator, final String attributeName) {
        return locator.getAttribute(attributeName);
    }

    /**
     * Check if an element is checked (for checkboxes/radio buttons).
     *
     * @param locator the element locator
     * @return true if checked, false otherwise
     */
    public static boolean isChecked(final Locator locator) {
        return locator.isChecked();
    }

    /**
     * Check if an element is visible.
     *
     * @param locator the element locator
     * @return true if visible, false otherwise
     */
    public static boolean isVisible(final Locator locator) {
        try {
            return locator.isVisible();
        } catch (final Exception e) {
            logger.trace("Element visibility check failed", e);
            return false;
        }
    }

    /**
     * Check if an element is enabled.
     *
     * @param locator the element locator
     * @return true if enabled, false otherwise
     */
    public static boolean isEnabled(final Locator locator) {
        try {
            return locator.isEnabled();
        } catch (final Exception e) {
            logger.trace("Element enabled check failed", e);
            return false;
        }
    }

    /**
     * Get the count of elements matching a locator.
     *
     * @param locator the element locator
     * @return the count of matching elements
     */
    public static int getElementCount(final Locator locator) {
        return locator.count();
    }

    /**
     * Exception thrown on timeout.
     */
    public static class TimeoutException extends RuntimeException {
        public TimeoutException(final String message) {
            super(message);
        }
    }
}
