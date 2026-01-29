package llm.runner;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import llm.model.LLMTestPlan;
import llm.parser.AmbiguityDetector;
import llm.parser.FeatureFileGenerator;
import llm.parser.LLMParser;
import llm.parser.LLMValidator;
import llm.parser.StepDefinitionGenerator;

public class LLMRunner {
        public static void main(String[] args) throws Exception {
                // Read requirement
                String requirementText = Files.readString(
                                Path.of("llm/input/requirements.txt"));

                // Read prompt template
                String promptTemplate = Files.readString(
                                Path.of("llm/prompts/generate_bdd.prompt"));

                // Inject requirement
                String finalPrompt = promptTemplate.replace(
                                "{{REQUIREMENT_TEXT}}",
                                requirementText);

                Path mockOutputPath = Path.of("llm/output/mock_json/risk_based.json");
                String llmResponse = Files.readString(mockOutputPath);
                if (llmResponse.contains("=")) {
                        throw new RuntimeException(
                                        "Invalid LLM output: Map-style syntax detected. Expected JSON.");
                }

                LLMParser parser = new LLMParser();
                LLMTestPlan plan = parser.parse(mockOutputPath.toFile());

                LLMValidator.validate(plan);

                List<String> ambiguities = AmbiguityDetector.detect(plan);
                if (!ambiguities.isEmpty()) {
                        System.out.println("Detected ambiguities:");
                        ambiguities.forEach(System.out::println);
                }

                // Generate feature files
                FeatureFileGenerator.generateFeatureFiles(plan);

                // Genereate step definations
                StepDefinitionGenerator.generateStepDefinitions();

        }

        private static String callLLM(String prompt) throws Exception {

                String apiKey = System.getenv("OPENAI_API_KEY");
                if (apiKey == null) {
                        throw new RuntimeException("OPENAI_API_KEY not set");
                }

                String requestBody = """
                                {
                                  "model": "gpt-4o-mini",
                                  "messages": [
                                    { "role": "user", "content": %s }
                                  ],
                                  "temperature": 0
                                }
                                """.formatted(jsonEscape(prompt));

                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                                .header("Content-Type", "application/json")
                                .header("Authorization", "Bearer " + apiKey)
                                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                                .build();

                HttpClient client = HttpClient.newHttpClient();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                return response.body();
        }

        private static String jsonEscape(String text) {
                return "\"" + text
                                .replace("\\", "\\\\")
                                .replace("\"", "\\\"")
                                .replace("\n", "\\n") + "\"";

        }
}
