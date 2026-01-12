package cucumber;

import com.google.gson.JsonObject;
import io.cucumber.java.After;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Cucumber hooks for API test lifecycle management.
 * Logs and embeds API request/response messages in Cucumber reports.
 */
public class APIHooks {

    private static final Logger logger = LoggerFactory.getLogger(APIHooks.class);

    @After(order = 10) // Run after test steps but before other hooks
    public void afterApiScenario(final io.cucumber.java.Scenario scenario) {
        logger.info("Processing API scenario: {}", scenario.getName());

        final JsonObject request = ScenarioContext.getApiRequest();
        final JsonObject response = ScenarioContext.getApiResponse();
        final Integer responseStatus = ScenarioContext.getApiResponseStatus();

        // Log and embed API request/response if available
        if (request != null || response != null) {
            try {
                final StringBuilder apiInfo = new StringBuilder();

                // Add request details
                if (request != null) {
                    apiInfo.append("=== API REQUEST ===\n");
                    apiInfo.append(request.toString()).append("\n\n");
                    logger.debug("✓ Request captured: {}", request);
                }

                // Add response status
                if (responseStatus != null) {
                    apiInfo.append("=== RESPONSE STATUS ===\n");
                    apiInfo.append("Status Code: ").append(responseStatus).append("\n\n");
                    logger.debug("✓ Response status: {}", responseStatus);
                }

                // Add response details
                if (response != null) {
                    apiInfo.append("=== API RESPONSE ===\n");
                    apiInfo.append(response.toString()).append("\n\n");
                    logger.debug("✓ Response captured: {}", response);
                }

                // Attach to Cucumber report
                if (apiInfo.length() > 0) {
                    scenario.attach(apiInfo.toString(), "text/plain", "API Request/Response");
                    logger.info("✓ API request/response embedded in report");
                }
            } catch (final Exception e) {
                logger.warn("Failed to embed API request/response in report for scenario: {}",
                        scenario.getName(), e);
            }
        }
    }
}
