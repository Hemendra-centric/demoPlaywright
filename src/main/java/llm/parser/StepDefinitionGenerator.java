package llm.parser;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StepDefinitionGenerator {

    private static final String OUTPUT_DIR = "llm/output/step_definitions/";

    public static void generateStepDefinitions() throws Exception {
        // Create output dir if not exists
        File dir = new File(OUTPUT_DIR);
        if (!dir.exists())
            dir.mkdirs();

        // Scan feature files
        Files.list(Paths.get("llm/output/features/"))
                .filter(path -> path.toString().endsWith(".feature"))
                .forEach(StepDefinitionGenerator::processFeatureFile);
    }

    private static void processFeatureFile(Path featurePath) {
        try {
            List<String> lines = Files.readAllLines(featurePath);
            String className = featurePath.getFileName().toString()
                    .replace(".feature", "Steps")
                    .replaceAll("[^a-zA-Z0-9]", "_");

            File javaFile = new File(OUTPUT_DIR + className + ".java");
            try (PrintWriter writer = new PrintWriter(new FileWriter(javaFile))) {
                writer.println("package llm.parser;");
                writer.println();
                writer.println("import io.cucumber.java.en.*;");
                writer.println();
                writer.println("public class " + className + " {");
                writer.println();

                Pattern stepPattern = Pattern.compile("^(Given|When|Then|And)\\s+(.*)");

                for (String line : lines) {
                    Matcher matcher = stepPattern.matcher(line.trim());
                    if (matcher.find()) {
                        String stepType = matcher.group(1);
                        String stepText = matcher.group(2)
                                .replace("\"", "\\\"");

                        // Generate method stub
                        writer.println("    @" + stepType + "(\"" + stepText + "\")");
                        writer.println("    public void " + generateMethodName(stepText) + "() {");
                        writer.println("        // TODO: implement step");
                        writer.println("    }\n");
                    }
                }

                writer.println("}");
            }

            System.out.println("Step definition file generated: " + javaFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Converts step text into a valid method name
    private static String generateMethodName(String stepText) {
        stepText = stepText.toLowerCase()
                .replaceAll("[^a-zA-Z0-9 ]", "")
                .replaceAll("\\s+", "_");
        return stepText;
    }
}
