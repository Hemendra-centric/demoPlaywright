package llm.model;

import java.util.List;

public class LLMTestPlan {

    private String requirementId;
    private String summary;

    private UIFeature ui;
    private APIFeature api;

    private List<String> ambiguities;
    private List<String> assumptions;
    private List<String> outOfScope;

    // Getters and Setters
    public String getRequirementId() {
        return requirementId;
    }

    public void setRequirementId(String requirementId) {
        this.requirementId = requirementId;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public UIFeature getUi() {
        return ui;
    }

    public void setUi(UIFeature ui) {
        this.ui = ui;
    }

    public APIFeature getApi() {
        return api;
    }

    public void setApi(APIFeature api) {
        this.api = api;
    }

    public List<String> getAmbiguities() {
        return ambiguities;
    }

    public void setAmbiguities(List<String> ambiguities) {
        this.ambiguities = ambiguities;
    }

    public List<String> getAssumptions() {
        return assumptions;
    }

    public void setAssumptions(List<String> assumptions) {
        this.assumptions = assumptions;
    }

    public List<String> getOutOfScope() {
        return outOfScope;
    }

    public void setOutOfScope(List<String> outOfScope) {
        this.outOfScope = outOfScope;
    }
}
