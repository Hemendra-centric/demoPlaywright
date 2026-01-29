package llm.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import llm.model.LLMTestPlan;

import java.io.File;

public class LLMParser {

    private final ObjectMapper mapper = new ObjectMapper();

    public LLMTestPlan parse(File llmResponseFile) {
        try {
            return mapper.readValue(llmResponseFile, LLMTestPlan.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse LLM JSON", e);
        }
    }
}
