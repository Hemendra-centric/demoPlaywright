package llm;

public class LLMTestPlan {
    private String requirementId;
    private String requirementSummary;

    private List<Ambiguity> ambiguities;
    private RiskClassification riskClassification;

    private List<UiFeature> uiFeatures;
    private List<ApiFeature> apiFeatures;

    private TraceabilityMap traceabilityMap;
}
