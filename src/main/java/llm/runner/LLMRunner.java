package llm;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;

import com.aventstack.extentreports.gherkin.model.Then;

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

                // Call LLM
                // String llmResponse = callLLM(finalPrompt); // To be used for real api calls

                // Write raw output
                // Path outputPath = Path.of("llm/output/llm_raw_response.txt");
                // Files.writeString(outputPath, llmResponse);

                Path mockOutputPath = Path.of("llm/output/llm_raw_response.txt");
                String llmResponse = Files.readString(mockOutputPath);

                // Print to console (optional, just to verify)
                System.out.println("=== MOCK LLM RESPONSE START ===");
                System.out.println(llmResponse);
                System.out.println("=== MOCK LLM RESPONSE END ===");
                System.out.println("LLM response written to llm/output/llm_raw_response.txt");
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
