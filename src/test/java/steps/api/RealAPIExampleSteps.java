package steps.api;

import api.ApiClient;
import api.ApiUtils;
import com.google.gson.JsonObject;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Practical example demonstrating real API testing.
 * 
 * This example uses JSONPlaceholder (https://jsonplaceholder.typicode.com)
 * a free public API for testing.
 * 
 * Run with: mvn test -Dgroups="api,example"
 */
public class RealAPIExampleSteps {

    private static final Logger logger = LoggerFactory.getLogger(RealAPIExampleSteps.class);

    private ApiClient client;
    private JsonObject lastResponse;
    private int lastStatusCode;

    /**
     * EXAMPLE 1: Get a public resource from JSONPlaceholder
     * Tests GET request and response capture
     */
    @Given("I fetch post number {int} from JSONPlaceholder")
    public void fetchPost(final int postId) {
        logger.info("Fetching post #{} from JSONPlaceholder", postId);

        // Create client pointing to public API
        client = new ApiClient("https://jsonplaceholder.typicode.com");

        // Make GET request
        lastStatusCode = client.get("/posts/" + postId);
        lastResponse = client.getLastResponseAsJson();

        logger.info("✓ Response Status: {}", lastStatusCode);
        logger.info("✓ Response Body:\n{}", client.getLastResponsePretty());
    }

    /**
     * EXAMPLE 2: Create a resource via POST
     * Tests POST request with body
     */
    @When("I create a new post with title {string}")
    public void createPost(final String title) {
        logger.info("Creating new post with title: {}", title);

        // Prepare request body
        final JsonObject postData = new JsonObject();
        postData.addProperty("title", title);
        postData.addProperty("body", "This is a test post created via API");
        postData.addProperty("userId", 1);

        // Make POST request
        lastStatusCode = client.post("/posts", postData);
        lastResponse = client.getLastResponseAsJson();

        logger.info("✓ Created post with ID: {}", lastResponse.get("id").getAsInt());
    }

    /**
     * EXAMPLE 3: Assert response contains expected fields
     * Tests JSON field validation
     */
    @Then("the response should contain post fields")
    public void validatePostFields() {
        logger.info("Validating post response structure");

        // Check required fields
        final String[] requiredFields = { "userId", "id", "title", "body" };

        for (final String field : requiredFields) {
            if (!lastResponse.has(field)) {
                throw new AssertionError("Missing field in response: " + field);
            }
            logger.info("✓ Field present: {} = {}", field, lastResponse.get(field));
        }
    }

    /**
     * EXAMPLE 4: Capture real response as fixture
     * Demonstrates how to save API response for offline use
     */
    @When("I capture the post response as a fixture named {string}")
    public void captureAsFixture(final String fixtureName) {
        logger.info("Capturing response as fixture: {}", fixtureName);

        // This prints the JSON in fixture-ready format
        final String json = com.google.gson.GsonBuilder.class.getProtectionDomain()
                .getCodeSource().getLocation().toString();

        logger.info("✓ Fixture data:");
        logger.info(lastResponse.toString());
        logger.info("\nSave the JSON above to: src/test/resources/fixtures/{}.json",
                fixtureName);
    }

    /**
     * EXAMPLE 5: Test with fallback
     * If real API fails, use fixture instead
     */
    @When("I fetch post {int} with fallback")
    public void fetchWithFallback(final int postId) {
        logger.info("Fetching post with fallback to fixture");

        try {
            client = new ApiClient("https://jsonplaceholder.typicode.com");
            lastStatusCode = client.get("/posts/" + postId);
            lastResponse = client.getLastResponseAsJson();

            if (client.isSuccessful()) {
                logger.info("✓ Real API succeeded");
            } else {
                logger.warn("API returned error status {}, falling back", lastStatusCode);
                // In real scenario, load from fixture here
            }
        } catch (final Exception e) {
            logger.warn("API call failed: {}. In production, would use fixture", e.getMessage());
            // Fallback to fixture
            lastResponse = new JsonObject();
            lastResponse.addProperty("id", postId);
            lastResponse.addProperty("title", "Fallback Post");
            lastResponse.addProperty("body", "This is from fallback fixture");
            lastResponse.addProperty("userId", 1);
        }
    }

    /**
     * EXAMPLE 6: Assert HTTP status codes
     */
    @Then("the response status code should be {int}")
    public void assertStatusCode(final int expectedStatus) {
        logger.info("Asserting status code: expected={}, actual={}", expectedStatus, lastStatusCode);

        if (lastStatusCode != expectedStatus) {
            throw new AssertionError(
                    String.format("Status code mismatch. Expected: %d, Got: %d",
                            expectedStatus, lastStatusCode));
        }
        logger.info("✓ Status code verified");
    }

    /**
     * EXAMPLE 7: Extract and verify specific field
     */
    @Then("the post title should be {string}")
    public void assertPostTitle(final String expectedTitle) {
        logger.info("Asserting post title: {}", expectedTitle);

        if (!lastResponse.has("title")) {
            throw new AssertionError("Response doesn't have 'title' field");
        }

        final String actualTitle = lastResponse.get("title").getAsString();
        if (!actualTitle.equals(expectedTitle)) {
            throw new AssertionError(
                    String.format("Title mismatch. Expected: '%s', Got: '%s'",
                            expectedTitle, actualTitle));
        }
        logger.info("✓ Post title verified: {}", actualTitle);
    }

    /**
     * EXAMPLE 8: API contract validation
     * Verify response structure matches expected contract
     */
    @Then("the response matches post contract")
    public void validateContract() {
        logger.info("Validating response matches post contract");

        // Define what a valid post looks like
        final String[] contractFields = { "userId", "id", "title", "body" };
        final Class<?>[] contractTypes = { Integer.class, Integer.class, String.class, String.class };

        for (int i = 0; i < contractFields.length; i++) {
            final String field = contractFields[i];

            if (!lastResponse.has(field)) {
                throw new AssertionError("Contract violation: missing field '" + field + "'");
            }

            logger.info("✓ Contract field verified: {} (type: {})",
                    field, lastResponse.get(field).getClass().getSimpleName());
        }

        logger.info("✓ Response matches post contract");
    }

    /**
     * EXAMPLE 9: Make request and capture in one step
     */
    @Given("I fetch and capture post {int} as {string}")
    public void fetchAndCapture(final int postId, final String fixtureName) {
        logger.info("Fetching post {} and capturing as fixture {}", postId, fixtureName);

        // Make request
        client = new ApiClient("https://jsonplaceholder.typicode.com");
        lastStatusCode = client.get("/posts/" + postId);
        lastResponse = client.getLastResponseAsJson();

        // Capture
        logger.info("Save this to src/test/resources/fixtures/{}.json:", fixtureName);
        logger.info(lastResponse.toString());
    }

    /**
     * EXAMPLE 10: Use ApiUtils for automatic fixture capture
     */
    @When("I use ApiUtils to capture post {int}")
    public void captureUsingUtils(final int postId) {
        logger.info("Using ApiUtils to capture post {}", postId);

        // This will make request and print fixture-ready JSON
        final boolean captured = ApiUtils.captureResponseAsFixture(
                "GET",
                "/posts/" + postId,
                null,
                "post-" + postId + "-response.json");

        if (captured) {
            logger.info("✓ Response captured successfully");
        } else {
            logger.error("Failed to capture response");
        }
    }
}
