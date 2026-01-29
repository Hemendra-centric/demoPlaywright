package llm.model;

import java.util.List;

public class UIFeature {

    private String featureName;
    private String feature; // The Gherkin feature text
    private List<UIScenario> scenarios;

    // Getters and setters
    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public List<UIScenario> getScenarios() {
        return scenarios;
    }

    public void setScenarios(List<UIScenario> scenarios) {
        this.scenarios = scenarios;
    }
}
