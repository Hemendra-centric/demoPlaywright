package utils;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;

/**
 * Utilities for mocking HTTP requests and responses in tests.
 * Supports intercepting routes and providing stubbed responses.
 */
public final class MockHelper {

    private static final Logger logger = LoggerFactory.getLogger(MockHelper.class);

    private MockHelper() {
        // Utility class - no instantiation
    }

    /**
     * Mock a specific URL pattern to return a success response.
     *
     * @param page         the Playwright page
     * @param urlPattern   the URL pattern to match (regex)
     * @param responseJson the JSON response body
     * @param statusCode   the HTTP status code
     */
    public static void mockRoute(
            final Page page,
            final String urlPattern,
            final String responseJson,
            final int statusCode) {
        page.route(urlPattern, route -> {
            final Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");

            route.fulfill(new Route.FulfillOptions()
                    .setStatus(statusCode)
                    .setHeaders(headers)
                    .setBody(responseJson));
            logger.debug("✓ Mocked route: {} -> {}", urlPattern, statusCode);
        });
    }

    /**
     * Mock a route to return a fixture-based response.
     *
     * @param page            the Playwright page
     * @param urlPattern      the URL pattern to match (regex)
     * @param fixtureFileName the fixture file name
     * @param statusCode      the HTTP status code
     */
    public static void mockRouteWithFixture(
            final Page page,
            final String urlPattern,
            final String fixtureFileName,
            final int statusCode) {
        final String responseJson = FixtureLoader.loadFixtureAsString(fixtureFileName);
        mockRoute(page, urlPattern, responseJson, statusCode);
    }

    /**
     * Mock a route to return a failure response.
     *
     * @param page         the Playwright page
     * @param urlPattern   the URL pattern to match (regex)
     * @param statusCode   the HTTP error code
     * @param errorMessage the error message
     */
    public static void mockRouteError(
            final Page page,
            final String urlPattern,
            final int statusCode,
            final String errorMessage) {
        final String errorJson = String.format("{\"error\": \"%s\"}", errorMessage);
        mockRoute(page, urlPattern, errorJson, statusCode);
    }

    /**
     * Mock a route to be aborted (network error).
     *
     * @param page       the Playwright page
     * @param urlPattern the URL pattern to match (regex)
     */
    public static void mockRouteAbort(final Page page, final String urlPattern) {
        page.route(urlPattern, route -> {
            route.abort();
            logger.debug("✓ Aborted route: {}", urlPattern);
        });
    }

    /**
     * Unmock a route by stopping route interception.
     *
     * @param page       the Playwright page
     * @param urlPattern the URL pattern to stop intercepting
     */
    public static void unmockRoute(final Page page, final String urlPattern) {
        page.unroute(urlPattern);
        logger.debug("✓ Unrouted: {}", urlPattern);
    }

    /**
     * Clear all mocked routes on a page.
     *
     * @param page the Playwright page
     */
    public static void clearAllMocks(final Page page) {
        page.unroute("**/*");
        logger.debug("✓ Cleared all mocked routes");
    }
}
