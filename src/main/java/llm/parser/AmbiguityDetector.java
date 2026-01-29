package llm.parser;

import llm.model.APIFeature;
import llm.model.APIScenario;
import llm.model.LLMTestPlan;
import llm.model.UIFeature;
import llm.model.UIScenario;

import java.util.ArrayList;
import java.util.List;

public class AmbiguityDetector {

    public static List<String> detect(LLMTestPlan plan) {
        List<String> ambiguities = new ArrayList<>();

        // Add LLM-reported ambiguities
        if (plan.getAmbiguities() != null) {
            ambiguities.addAll(plan.getAmbiguities());
        }

        // Check UI scenarios
        // Check UI scenarios
        UIFeature ui = plan.getUi();
        if (ui != null && ui.getScenarios() != null) {
            List<UIScenario> uiScenarios = ui.getScenarios();

            for (UIScenario sc : uiScenarios) {
                if (sc.getSteps() == null || sc.getSteps().isEmpty()) {
                    ambiguities.add("UI scenario '" + sc.getName() + "' has no steps defined");
                }
            }
        }

        // Check API scenarios
        APIFeature api = plan.getApi();
        if (api != null && api.getScenarios() != null) {
            List<APIScenario> apiScenarios = api.getScenarios();

            for (APIScenario sc : apiScenarios) {
                boolean missingRequest = sc.getRequest() == null || sc.getRequest().isEmpty();
                boolean missingResponse = sc.getExpectedResponse() == null || sc.getExpectedResponse().isEmpty();

                if (missingRequest || missingResponse) {
                    ambiguities.add("API scenario '" + sc.getName() + "' is missing " +
                            (missingRequest ? "request" : "") +
                            (missingRequest && missingResponse ? " and " : "") +
                            (missingResponse ? "expected response" : ""));
                }
            }
        }

        return ambiguities;
    }
}
