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

    @After(order = 10) // Run after other hooks (BrowserHooks is order 1)
    public void afterApiScenario(final io.cucumber.java.Scenario scenario) {
        logger.info("Processing API scenario: {}", scenario.getName());

        final JsonObject request = ScenarioContext.getApiRequest();
        final JsonObject response = ScenarioContext.getApiResponse();
        final Integer responseStatus = ScenarioContext.getApiResponseStatus();

        logger.info("API data check - Request: {}, Response: {}, Status: {}",
                (request != null ? "YES" : "NO"),
                (response != null ? "YES" : "NO"),
                (responseStatus != null ? responseStatus : "NO"));

        // Log and embed API request/response if available
        if (request != null || response != null) {
            try {
                final StringBuilder apiInfo = new StringBuilder();

                // Add request details
                if (request != null) {
                    apiInfo.append("=== API REQUEST ===\n");
                    apiInfo.append(request.toString()).append("\n\n");
                    logger.info("✓ Request captured: {}", request);
                }

                // Add response status
                if (responseStatus != null) {
                    apiInfo.append("=== RESPONSE STATUS ===\n");
                    apiInfo.append("Status Code: ").append(responseStatus).append("\n\n");
                    logger.info("✓ Response status: {}", responseStatus);
                }

                // Add response details
                if (response != null) {
                    apiInfo.append("=== API RESPONSE ===\n");
                    apiInfo.append(response.toString()).append("\n\n");
                    logger.info("✓ Response captured: {}", response);
                }

                // Attach to Cucumber report
                if (apiInfo.length() > 0) {
                    scenario.attach(apiInfo.toString(), "text/plain", "API Request/Response");
                    logger.info("✓ API request/response embedded in report for: {}", scenario.getName());
                }
            } catch (final Exception e) {
                logger.warn("Failed to embed API request/response in report for scenario: {}",
                        scenario.getName(), e);
            }
        } else {
            logger.info("⚠️ No API data found for scenario: {}", scenario.getName());
        }

        // Now cleanup after API data is processed
        ScenarioContext.cleanup();
        logger.debug("✓ @After(order=10) APIHooks completed with cleanup");
    }
}
