package llm.model;

import java.util.List;

public class UIScenario {

    private String name;
    private String type; // HAPPY_PATH, NEGATIVE
    private String risk; // HIGH, MEDIUM, LOW
    private List<String> steps;

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRisk() {
        return risk;
    }

    public void setRisk(String risk) {
        this.risk = risk;
    }

    public List<String> getSteps() {
        return steps;
    }

    public void setSteps(List<String> steps) {
        this.steps = steps;
    }
}
