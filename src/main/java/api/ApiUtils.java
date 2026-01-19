package api;

import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ConfigReader;
import utils.FixtureLoader;

/**
 * API utilities for flexible API testing.
 * Supports switching between real API calls and mocked responses.
 * Allows capturing real request/response data for fixtures.
 */
public final class ApiUtils {

    private static final Logger logger = LoggerFactory.getLogger(ApiUtils.class);

    private ApiUtils() {
        // Utility class - no instantiation
    }

    /**
     * Create or get an API client configured from properties.
     *
     * @return ApiClient instance
     */
    public static ApiClient createApiClient() {
        final String baseUri = ConfigReader.get("api.base.uri", "");
        if (baseUri.isEmpty()) {
            throw new RuntimeException(
                    "api.base.uri not configured in config.properties. "
                            + "Please set it to test against real APIs or mock server.");
        }

        final ApiClient client = new ApiClient(baseUri);

        // Add default headers
        final String apiKey = ConfigReader.get("api.key", "");
        if (!apiKey.isEmpty()) {
            client.addHeader("X-API-Key", apiKey);
        }

        logger.info("✓ API Client initialized with base URI: {}", baseUri);
        return client;
    }

    /**
     * Make a real API request and capture response.
     * Useful for generating fixtures or integration testing.
     *
     * @param method      the HTTP method (GET, POST, PUT, DELETE)
     * @param endpoint    the endpoint path
     * @param requestBody the request body (null for GET/DELETE)
     * @return the captured response as JsonObject
     */
    public static JsonObject makeRealRequest(
            final String method,
            final String endpoint,
            final JsonObject requestBody) {

        logger.info("Making REAL API request: {} {}", method, endpoint);

        try {
            final ApiClient client = createApiClient();
            int statusCode = -1;

            switch (method.toUpperCase()) {
                case "GET":
                    statusCode = client.get(endpoint);
                    break;
                case "POST":
                    statusCode = requestBody != null
                            ? client.post(endpoint, requestBody)
                            : client.post(endpoint, "{}");
                    break;
                case "PUT":
                    statusCode = requestBody != null
                            ? client.put(endpoint, requestBody)
                            : client.put(endpoint, "{}");
                    break;
                case "DELETE":
                    statusCode = client.delete(endpoint);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported HTTP method: " + method);
            }

            if (client.isSuccessful()) {
                logger.info("✓ Real API request successful (status: {})", statusCode);
                return client.getLastResponseAsJson();
            } else {
                logger.warn("⚠ API request failed with status: {}", statusCode);
                final JsonObject error = new JsonObject();
                error.addProperty("error", true);
                error.addProperty("status", statusCode);
                error.addProperty("message", client.getLastResponseBody());
                return error;
            }
        } catch (final Exception e) {
            logger.error("Failed to make real API request", e);
            final JsonObject error = new JsonObject();
            error.addProperty("error", true);
            error.addProperty("exception", e.getMessage());
            return error;
        }
    }

    /**
     * Execute request with fallback: try real API, fallback to fixture if it fails.
     *
     * @param endpoint        the endpoint path
     * @param requestBody     the request body for POST
     * @param fallbackFixture the fixture file name to use on failure
     * @return response object
     */
    public static JsonObject requestWithFallback(
            final String endpoint,
            final JsonObject requestBody,
            final String fallbackFixture) {

        logger.info("Attempting request with fallback: {} (fallback: {})", endpoint, fallbackFixture);

        try {
            final ApiClient client = createApiClient();
            final int statusCode = client.post(endpoint, requestBody);

            if (client.isSuccessful()) {
                logger.info("✓ Real API request succeeded");
                return client.getLastResponseAsJson();
            } else {
                logger.warn("API request failed with status {}. Using fallback fixture: {}",
                        statusCode, fallbackFixture);
                return FixtureLoader.loadFixture(fallbackFixture, JsonObject.class);
            }
        } catch (final Exception e) {
            logger.warn("Real API request failed ({}). Falling back to fixture: {}",
                    e.getMessage(), fallbackFixture);
            return FixtureLoader.loadFixture(fallbackFixture, JsonObject.class);
        }
    }

    /**
     * Compare real API response with expected fixture for contract testing.
     * Useful for validating API contracts haven't changed.
     *
     * @param endpoint        the endpoint to test
     * @param fixtureFileName the fixture to compare against
     * @return true if API response matches fixture structure
     */
    public static boolean validateApiContract(final String endpoint, final String fixtureFileName) {
        logger.info("Validating API contract for endpoint: {}", endpoint);

        try {
            final ApiClient client = createApiClient();
            final int statusCode = client.get(endpoint);

            if (!client.isSuccessful()) {
                logger.error("API contract validation failed: HTTP {}", statusCode);
                return false;
            }

            final JsonObject actualResponse = client.getLastResponseAsJson();
            final JsonObject expectedResponse = FixtureLoader.loadFixture(fixtureFileName, JsonObject.class);

            // Check if all keys in expected response exist in actual response
            boolean matches = true;
            for (final String key : expectedResponse.keySet()) {
                if (!actualResponse.has(key)) {
                    logger.warn("Contract mismatch: missing key '{}'", key);
                    matches = false;
                }
            }

            if (matches) {
                logger.info("✓ API contract validated successfully");
            }
            return matches;
        } catch (final Exception e) {
            logger.error("Failed to validate API contract", e);
            return false;
        }
    }

    /**
     * Capture a real API response and save it as a fixture for future use.
     * Great for creating baseline fixtures from live APIs.
     *
     * @param method            the HTTP method
     * @param endpoint          the endpoint path
     * @param requestBody       the request body (null for GET)
     * @param outputFixtureName the fixture file name to save
     * @return true if capture succeeded
     */
    public static boolean captureResponseAsFixture(
            final String method,
            final String endpoint,
            final JsonObject requestBody,
            final String outputFixtureName) {

        logger.info("Capturing API response as fixture: {}", outputFixtureName);

        try {
            final JsonObject response = makeRealRequest(method, endpoint, requestBody);

            if (response.has("error")) {
                logger.error("Cannot capture error response as fixture");
                return false;
            }

            // Save to fixtures directory
            final String jsonString = FixtureLoader.toJson(response);
            logger.info("✓ Response captured and formatted for fixture");
            logger.info("Save this JSON to src/test/resources/fixtures/{}: \n{}",
                    outputFixtureName, jsonString);

            return true;
        } catch (final Exception e) {
            logger.error("Failed to capture response as fixture", e);
            return false;
        }
    }
}
