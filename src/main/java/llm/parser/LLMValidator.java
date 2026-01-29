package llm.parser;

import llm.model.LLMTestPlan;
import llm.model.UIFeature;
import llm.model.UIScenario;
import llm.model.APIFeature;
import llm.model.APIScenario;

import java.util.List;

public class LLMValidator {

    public static void validate(LLMTestPlan plan) {
        // Validate requirement ID
        if (plan.getRequirementId() == null || plan.getRequirementId().isEmpty()) {
            throw new RuntimeException("requirementId missing");
        }

        // Validate UI feature
        UIFeature ui = plan.getUi();
        if (ui == null || ui.getFeatureName() == null || ui.getFeatureName().isEmpty()) {
            throw new RuntimeException("UI featureName missing");
        }

        List<UIScenario> uiScenarios = ui.getScenarios();
        if (uiScenarios == null || uiScenarios.isEmpty()) {
            throw new RuntimeException("No UI scenarios defined");
        }

        // Validate API feature
        APIFeature api = plan.getApi();
        if (api == null || api.getFeatureName() == null || api.getFeatureName().isEmpty()) {
            throw new RuntimeException("API featureName missing");
        }

        List<APIScenario> apiScenarios = api.getScenarios();
        if (apiScenarios == null || apiScenarios.isEmpty()) {
            throw new RuntimeException("No API scenarios defined");
        }

        // Optional: further per-scenario validation
        for (UIScenario sc : uiScenarios) {
            if (sc.getSteps() == null || sc.getSteps().isEmpty()) {
                throw new RuntimeException("UI scenario '" + sc.getName() + "' has no steps defined");
            }
        }

        for (APIScenario sc : apiScenarios) {
            if (sc.getRequest() == null || sc.getRequest().isEmpty() ||
                    sc.getExpectedResponse() == null || sc.getExpectedResponse().isEmpty()) {
                throw new RuntimeException(
                        "API scenario '" + sc.getName() + "' is missing request or expected response");
            }
        }
    }
}
