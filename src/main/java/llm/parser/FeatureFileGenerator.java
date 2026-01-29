package llm.parser;

import llm.model.LLMTestPlan;
import llm.model.UIScenario;
import llm.model.UIFeature;
import llm.model.APIScenario;
import llm.model.APIFeature;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

public class FeatureFileGenerator {

    private static final String OUTPUT_DIR = "llm/output/features/";

    public static void generateFeatureFiles(LLMTestPlan plan) throws Exception {
        // Create output dir if not exists
        File dir = new File(OUTPUT_DIR);
        if (!dir.exists())
            dir.mkdirs();

        generateUIFeatures(plan);
        generateAPIFeatures(plan);
    }

    private static void generateUIFeatures(LLMTestPlan plan) throws Exception {
        UIFeature uiFeature = plan.getUi();
        if (uiFeature == null || uiFeature.getScenarios() == null)
            return;

        String reqId = plan.getRequirementId();
        String featureName = sanitizeFileName(uiFeature.getFeatureName());

        File file = new File(OUTPUT_DIR + "ui_" + reqId + "_" + featureName + ".feature");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("Feature: " + uiFeature.getFeatureName() + "\n\n");

            List<UIScenario> scenarios = uiFeature.getScenarios();
            for (UIScenario scenario : scenarios) {
                writer.write("  Scenario: " + scenario.getName() + " [" + scenario.getType() + ", Risk: "
                        + scenario.getRisk() + "]\n");

                List<String> steps = scenario.getSteps();
                for (int i = 0; i < steps.size(); i++) {
                    String prefix;
                    if (i == 0)
                        prefix = "Given";
                    else if (i == steps.size() - 1)
                        prefix = "Then";
                    else
                        prefix = "When"; // or And for multiple intermediates

                    writer.write("    " + prefix + " " + steps.get(i) + "\n");
                }
                writer.write("\n");
            }
        }

        System.out.println("UI Feature file generated: " + file.getAbsolutePath());
    }

    private static void generateAPIFeatures(LLMTestPlan plan) throws Exception {
        APIFeature apiFeature = plan.getApi();
        if (apiFeature == null || apiFeature.getScenarios() == null)
            return;

        String reqId = plan.getRequirementId();
        String featureName = sanitizeFileName(apiFeature.getFeatureName());

        File file = new File(OUTPUT_DIR + "api_" + reqId + "_" + featureName + ".feature");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("Feature: " + apiFeature.getFeatureName() + " [API]\n\n");

            List<APIScenario> scenarios = apiFeature.getScenarios();
            for (APIScenario scenario : scenarios) {
                writer.write("  Scenario: " + scenario.getName() + " [Risk: " + scenario.getRisk() + "]\n");

                // Optional: auto-generate steps from request/response
                writer.write("    Given I send request: " + scenario.getRequest() + "\n");
                writer.write("    Then I verify response: " + scenario.getExpectedResponse() + "\n\n");
            }
        }

        System.out.println("API Feature file generated: " + file.getAbsolutePath());
    }

    private static String sanitizeFileName(String name) {
        return name.replaceAll("[^a-zA-Z0-9_\\-]", "_");
    }
}
