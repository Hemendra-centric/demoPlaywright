package utils;

import com.microsoft.playwright.Locator;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utilities for handling waits and retries in Playwright tests.
 * Provides explicit wait patterns and retry mechanisms.
 */
public final class WaitHelper {

    private static final Logger logger = LoggerFactory.getLogger(WaitHelper.class);
    private static final int DEFAULT_TIMEOUT_MS = 5000;
    private static final int DEFAULT_POLL_INTERVAL_MS = 500;

    private WaitHelper() {
        // Utility class - no instantiation
    }

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
     * Exception thrown on timeout.
     */
    public static class TimeoutException extends RuntimeException {
        public TimeoutException(final String message) {
            super(message);
        }
    }
}
