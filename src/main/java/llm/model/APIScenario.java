package llm.model;

import java.util.Map;

public class APIScenario {

    private String name;
    private String risk;
    private Map<String, Object> request; // Request body fields
    private Map<String, Object> expectedResponse; // Expected API response

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRisk() {
        return risk;
    }

    public void setRisk(String risk) {
        this.risk = risk;
    }

    public Map<String, Object> getRequest() {
        return request;
    }

    public void setRequest(Map<String, Object> request) {
        this.request = request;
    }

    public Map<String, Object> getExpectedResponse() {
        return expectedResponse;
    }

    public void setExpectedResponse(Map<String, Object> expectedResponse) {
        this.expectedResponse = expectedResponse;
    }
}
