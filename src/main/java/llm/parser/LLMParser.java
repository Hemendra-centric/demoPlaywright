package llm;

import java.nio.file.Files;
import java.nio.file.Path;

public class LLMParser {

    public static void main(String[] args) throws Exception {
        // Read raw LLM response
        String rawResponse = Files.readString(Path.of("llm/output/llm_raw_response.txt"));

        // Extract FEATURE part
        String featureContent = extractSection(rawResponse, "===FEATURE===");
        String stepsContent = extractSection(rawResponse, "===STEPS===");

        // Write feature file
        Path featurePath = Path.of("llm/output/generated.feature");
        Files.writeString(featurePath, featureContent);
        System.out.println("Feature file created at: " + featurePath.toAbsolutePath());

        // Write step skeletons
        Path stepsPath = Path.of("llm/output/StepDefinitions.java");
        Files.writeString(stepsPath, stepsContent);
        System.out.println("Step definitions created at: " + stepsPath.toAbsolutePath());
    }

    // Helper method to extract section from raw response
    private static String extractSection(String raw, String marker) {
        int index = raw.indexOf(marker);
        if (index == -1)
            return "";
        // Cut from marker to next marker or end
        int nextMarker = raw.indexOf("===", index + marker.length());
        if (nextMarker == -1)
            nextMarker = raw.length();
        return raw.substring(index + marker.length(), nextMarker).trim();
    }
}
