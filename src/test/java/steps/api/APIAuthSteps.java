package steps.api;

import com.google.gson.JsonObject;
import cucumber.ScenarioContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ConfigReader;
import utils.FixtureLoader;

/**
 * Step definitions for API authentication feature.
 * Demonstrates API testing patterns with fixtures and mock responses.
 */
public class APIAuthSteps {

    private static final Logger logger = LoggerFactory.getLogger(APIAuthSteps.class);

    private JsonObject requestBody;
    private int responseStatus;
    private JsonObject responseBody;

    @Given("I prepare the authentication API request")
    public void prepareAuthRequest() {
        logger.info("Step: Preparing authentication API request");
        requestBody = new JsonObject();
        ScenarioContext.setApiRequest(requestBody);
        logger.debug("✓ Authentication request prepared");
    }

    @When("I send a POST request to {string} with valid credentials")
    public void sendAuthRequestWithValidCredentials(final String endpoint) {
        logger.info("Step: Sending POST request to {} with valid credentials", endpoint);

        requestBody.addProperty("email", "user@example.com");
        requestBody.addProperty("password", "SecurePassword123");
        ScenarioContext.setApiRequest(requestBody);

        // In a real scenario, this would make an HTTP request via RestAssured or
        // similar
        // For demo, we'll mock the response
        responseStatus = 200;
        responseBody = new JsonObject();
        responseBody.addProperty("success", true);
        responseBody.addProperty("token", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9");
        responseBody.addProperty("userId", 1);
        responseBody.addProperty("email", "user@example.com");

        ScenarioContext.setApiResponse(responseBody);
        ScenarioContext.setApiResponseStatus(responseStatus);

        logger.info("✓ Mock response received with status: {}", responseStatus);
    }

    @When("I send a POST request to {string} with invalid credentials")
    public void sendAuthRequestWithInvalidCredentials(final String endpoint) {
        logger.info("Step: Sending POST request to {} with invalid credentials", endpoint);

        requestBody.addProperty("email", "invalid@example.com");
        requestBody.addProperty("password", "wrongpassword");
        ScenarioContext.setApiRequest(requestBody);

        // Mock response for invalid credentials
        responseStatus = 401;
        responseBody = new JsonObject();
        responseBody.addProperty("error", "INVALID_CREDENTIALS");
        responseBody.addProperty("message", "Invalid credentials");

        ScenarioContext.setApiResponse(responseBody);
        ScenarioContext.setApiResponseStatus(responseStatus);

        logger.info("✓ Mock response received with status: {}", responseStatus);
    }

    @When("I send a POST request to {string} with missing email")
    public void sendAuthRequestWithMissingEmail(final String endpoint) {
        logger.info("Step: Sending POST request to {} with missing email", endpoint);

        // Intentionally omit email
        requestBody.addProperty("password", "somepassword");
        ScenarioContext.setApiRequest(requestBody);

        // Mock response for missing email
        responseStatus = 401;
        responseBody = new JsonObject();
        responseBody.addProperty("error", "VALIDATION_ERROR");
        responseBody.addProperty("message", "Email is required");

        ScenarioContext.setApiResponse(responseBody);
        ScenarioContext.setApiResponseStatus(responseStatus);

        logger.info("✓ Mock response received with status: {}", responseStatus);
    }

    @Then("the response status should be {int}")
    public void verifyResponseStatus(final int expectedStatus) {
        logger.info("Step: Verifying response status is {}", expectedStatus);
        if (responseStatus != expectedStatus) {
            throw new AssertionError(
                    String.format("Expected status %d but got %d", expectedStatus, responseStatus));
        }
        logger.debug("✓ Response status verified: {}", responseStatus);
    }

    @Then("the response should contain a valid token")
    public void verifyValidToken() {
        logger.info("Step: Verifying response contains a valid token");
        if (!responseBody.has("token") || responseBody.get("token").isJsonNull()) {
            throw new AssertionError("Response does not contain a valid token");
        }
        logger.debug("✓ Valid token found in response");
    }

    @Then("the response should contain user details")
    public void verifyUserDetails() {
        logger.info("Step: Verifying response contains user details");
        if (!responseBody.has("userId") || !responseBody.has("email")) {
            throw new AssertionError("Response does not contain user details (userId or email)");
        }
        logger.debug("✓ User details found in response");
    }

    @Then("the response should contain error message {string}")
    public void verifyErrorMessage(final String expectedMessage) {
        logger.info("Step: Verifying response contains error message: {}", expectedMessage);
        if (!responseBody.has("message") || !responseBody.get("message").getAsString().equals(expectedMessage)) {
            throw new AssertionError(
                    String.format("Expected error message '%s' not found in response", expectedMessage));
        }
        logger.debug("✓ Error message verified: {}", expectedMessage);
    }
}
