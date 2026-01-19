package api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ConfigReader;

import java.util.HashMap;
import java.util.Map;

/**
 * HTTP API Client for making real API requests and capturing responses.
 * Supports both real API calls and mock-based testing.
 * Integrates with RestAssured for comprehensive API testing.
 */
public final class ApiClient {

    private static final Logger logger = LoggerFactory.getLogger(ApiClient.class);

    private String baseUri;
    private Map<String, String> headers;
    private int lastStatusCode;
    private String lastResponseBody;
    private Response lastResponse;

    public ApiClient(final String baseUri) {
        this.baseUri = baseUri;
        this.headers = new HashMap<>();
        this.headers.put("Content-Type", "application/json");
    }

    /**
     * Add a header to all subsequent requests.
     *
     * @param key   the header name
     * @param value the header value
     * @return this ApiClient for chaining
     */
    public ApiClient addHeader(final String key, final String value) {
        headers.put(key, value);
        logger.debug("✓ Added header: {} = {}", key, value);
        return this;
    }

    /**
     * Add authorization bearer token.
     *
     * @param token the JWT or API token
     * @return this ApiClient for chaining
     */
    public ApiClient addBearerToken(final String token) {
        addHeader("Authorization", "Bearer " + token);
        return this;
    }

    /**
     * Make a GET request to the specified endpoint.
     *
     * @param endpoint the endpoint path (e.g., "/api/users/1")
     * @return the response status code
     */
    public int get(final String endpoint) {
        logger.info("Making GET request to: {}", baseUri + endpoint);

        try {
            final RequestSpecification request = RestAssured.given()
                    .headers(headers)
                    .baseUri(baseUri);

            lastResponse = request.when().get(endpoint);
            lastStatusCode = lastResponse.getStatusCode();
            lastResponseBody = lastResponse.getBody().asString();

            logger.info("✓ GET request completed with status: {}", lastStatusCode);
            logger.debug("Response body: {}", lastResponseBody);

            return lastStatusCode;
        } catch (final Exception e) {
            logger.error("Failed to make GET request to {}", endpoint, e);
            lastStatusCode = -1;
            lastResponseBody = null;
            throw new RuntimeException("GET request failed: " + e.getMessage(), e);
        }
    }

    /**
     * Make a POST request with JSON body.
     *
     * @param endpoint    the endpoint path
     * @param requestBody the JSON request body
     * @return the response status code
     */
    public int post(final String endpoint, final JsonObject requestBody) {
        return post(endpoint, requestBody.toString());
    }

    /**
     * Make a POST request with raw JSON body.
     *
     * @param endpoint        the endpoint path
     * @param requestBodyJson the JSON request body as string
     * @return the response status code
     */
    public int post(final String endpoint, final String requestBodyJson) {
        logger.info("Making POST request to: {}", baseUri + endpoint);
        logger.debug("Request body: {}", requestBodyJson);

        try {
            final RequestSpecification request = RestAssured.given()
                    .headers(headers)
                    .baseUri(baseUri)
                    .body(requestBodyJson);

            lastResponse = request.when().post(endpoint);
            lastStatusCode = lastResponse.getStatusCode();
            lastResponseBody = lastResponse.getBody().asString();

            logger.info("✓ POST request completed with status: {}", lastStatusCode);
            logger.debug("Response body: {}", lastResponseBody);

            return lastStatusCode;
        } catch (final Exception e) {
            logger.error("Failed to make POST request to {}", endpoint, e);
            lastStatusCode = -1;
            lastResponseBody = null;
            throw new RuntimeException("POST request failed: " + e.getMessage(), e);
        }
    }

    /**
     * Make a PUT request with JSON body.
     *
     * @param endpoint    the endpoint path
     * @param requestBody the JSON request body
     * @return the response status code
     */
    public int put(final String endpoint, final JsonObject requestBody) {
        return put(endpoint, requestBody.toString());
    }

    /**
     * Make a PUT request with raw JSON body.
     *
     * @param endpoint        the endpoint path
     * @param requestBodyJson the JSON request body as string
     * @return the response status code
     */
    public int put(final String endpoint, final String requestBodyJson) {
        logger.info("Making PUT request to: {}", baseUri + endpoint);
        logger.debug("Request body: {}", requestBodyJson);

        try {
            final RequestSpecification request = RestAssured.given()
                    .headers(headers)
                    .baseUri(baseUri)
                    .body(requestBodyJson);

            lastResponse = request.when().put(endpoint);
            lastStatusCode = lastResponse.getStatusCode();
            lastResponseBody = lastResponse.getBody().asString();

            logger.info("✓ PUT request completed with status: {}", lastStatusCode);
            logger.debug("Response body: {}", lastResponseBody);

            return lastStatusCode;
        } catch (final Exception e) {
            logger.error("Failed to make PUT request to {}", endpoint, e);
            lastStatusCode = -1;
            lastResponseBody = null;
            throw new RuntimeException("PUT request failed: " + e.getMessage(), e);
        }
    }

    /**
     * Make a DELETE request.
     *
     * @param endpoint the endpoint path
     * @return the response status code
     */
    public int delete(final String endpoint) {
        logger.info("Making DELETE request to: {}", baseUri + endpoint);

        try {
            final RequestSpecification request = RestAssured.given()
                    .headers(headers)
                    .baseUri(baseUri);

            lastResponse = request.when().delete(endpoint);
            lastStatusCode = lastResponse.getStatusCode();
            lastResponseBody = lastResponse.getBody().asString();

            logger.info("✓ DELETE request completed with status: {}", lastStatusCode);
            logger.debug("Response body: {}", lastResponseBody);

            return lastStatusCode;
        } catch (final Exception e) {
            logger.error("Failed to make DELETE request to {}", endpoint, e);
            lastStatusCode = -1;
            lastResponseBody = null;
            throw new RuntimeException("DELETE request failed: " + e.getMessage(), e);
        }
    }

    /**
     * Get the last response status code.
     *
     * @return the HTTP status code
     */
    public int getLastStatusCode() {
        return lastStatusCode;
    }

    /**
     * Get the last response body as string.
     *
     * @return the response body
     */
    public String getLastResponseBody() {
        return lastResponseBody;
    }

    /**
     * Get the last response body as JsonObject.
     *
     * @return the response body as JsonObject
     */
    public JsonObject getLastResponseAsJson() {
        if (lastResponseBody == null || lastResponseBody.isEmpty()) {
            logger.warn("Response body is null or empty");
            return new JsonObject();
        }

        try {
            return JsonParser.parseString(lastResponseBody).getAsJsonObject();
        } catch (final Exception e) {
            logger.error("Failed to parse response body as JSON", e);
            return new JsonObject();
        }
    }

    /**
     * Get the raw RestAssured Response object for advanced assertions.
     *
     * @return the Response object
     */
    public Response getLastResponse() {
        return lastResponse;
    }

    /**
     * Check if last response is successful (2xx status).
     *
     * @return true if status code is between 200-299
     */
    public boolean isSuccessful() {
        return lastStatusCode >= 200 && lastStatusCode < 300;
    }

    /**
     * Pretty print the last response for debugging.
     *
     * @return formatted response details
     */
    public String getLastResponsePretty() {
        return String.format(
                "Status: %d\nHeaders: %s\nBody:\n%s",
                lastStatusCode,
                lastResponse != null ? lastResponse.getHeaders() : "N/A",
                lastResponseBody);
    }
}
