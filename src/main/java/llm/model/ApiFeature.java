package llm.model;

import java.util.List;

public class ApiFeature {
    private String featureName;
    private List<String> tags;

    private String endpoint;
    private String method;

    private List<String> requestParameters;
    private List<ApiResponse> expectedResponses;
}