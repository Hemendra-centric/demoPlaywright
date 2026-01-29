package llm.model;

import java.util.List;

public class APIFeature {

    private String featureName;
    private String endpoint;
    private String method; // POST, GET, etc.
    private List<APIScenario> scenarios;

    // Getters and setters
    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public List<APIScenario> getScenarios() {
        return scenarios;
    }

    public void setScenarios(List<APIScenario> scenarios) {
        this.scenarios = scenarios;
    }
}
