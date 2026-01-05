package utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Comprehensive reporting utility for test execution results.
 * Generates JSON reports, HTML summaries, and execution statistics.
 */
public final class ReportingUtil {

    private static final Logger logger = LoggerFactory.getLogger(ReportingUtil.class);
    private static final String REPORTS_DIR = "target/test-reports";
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private static final List<TestResult> testResults = new ArrayList<>();
    private static LocalDateTime testExecutionStart;
    private static LocalDateTime testExecutionEnd;

    private ReportingUtil() {
        // Utility class - no instantiation
    }

    /**
     * Initialize reporting system and create report directory.
     */
    public static void initialize() {
        try {
            Files.createDirectories(Paths.get(REPORTS_DIR));
            testExecutionStart = LocalDateTime.now();
            logger.info("✓ Reporting system initialized - Reports dir: {}", REPORTS_DIR);
        } catch (final IOException e) {
            logger.error("Failed to initialize reporting system", e);
        }
    }

    /**
     * Record a test result.
     *
     * @param testName the test name
     * @param status   the test status (PASSED, FAILED, SKIPPED)
     * @param duration the test duration in milliseconds
     * @param message  additional message/details
     */
    public static void recordTestResult(
            final String testName,
            final String status,
            final long duration,
            final String message) {
        final TestResult result = new TestResult(testName, status, duration, message);
        testResults.add(result);
        logger.info("Recorded test result: {} - {}", testName, status);
    }

    /**
     * Record test result with error.
     *
     * @param testName the test name
     * @param status   the test status
     * @param duration the test duration
     * @param error    the error/exception message
     */
    public static void recordTestResultWithError(
            final String testName,
            final String status,
            final long duration,
            final String error) {
        recordTestResult(testName, status, duration, "Error: " + error);
    }

    /**
     * Generate JSON report of all test results.
     *
     * @return path to the generated report file
     */
    public static String generateJsonReport() {
        testExecutionEnd = LocalDateTime.now();
        final ExecutionSummary summary = new ExecutionSummary(testResults, testExecutionStart, testExecutionEnd);

        try {
            final String fileName = String.format("%s/test-results-%s.json", REPORTS_DIR,
                    LocalDateTime.now().format(TIMESTAMP_FORMATTER));
            try (final FileWriter writer = new FileWriter(fileName)) {
                gson.toJson(summary, writer);
                logger.info("✓ JSON report generated: {}", fileName);
                return fileName;
            }
        } catch (final IOException e) {
            logger.error("Failed to generate JSON report", e);
            return null;
        }
    }

    /**
     * Generate HTML summary report.
     *
     * @return path to the generated HTML report
     */
    public static String generateHtmlReport() {
        try {
            final String fileName = String.format("%s/test-report-%s.html", REPORTS_DIR,
                    LocalDateTime.now().format(TIMESTAMP_FORMATTER));
            final String htmlContent = generateHtmlContent();

            try (final FileWriter writer = new FileWriter(fileName)) {
                writer.write(htmlContent);
                logger.info("✓ HTML report generated: {}", fileName);
                return fileName;
            }
        } catch (final IOException e) {
            logger.error("Failed to generate HTML report", e);
            return null;
        }
    }

    /**
     * Generate HTML content for report.
     *
     * @return HTML content as string
     */
    private static String generateHtmlContent() {
        final long totalTests = testResults.size();
        final long passedTests = testResults.stream().filter(r -> "PASSED".equals(r.status)).count();
        final long failedTests = testResults.stream().filter(r -> "FAILED".equals(r.status)).count();
        final long skippedTests = testResults.stream().filter(r -> "SKIPPED".equals(r.status)).count();
        final long totalDuration = testResults.stream().mapToLong(r -> r.duration).sum();
        final double passRate = totalTests > 0 ? (passedTests * 100.0) / totalTests : 0;

        final StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n");
        html.append("<html lang=\"en\">\n");
        html.append("<head>\n");
        html.append("    <meta charset=\"UTF-8\">\n");
        html.append("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        html.append("    <title>Test Execution Report</title>\n");
        html.append("    <style>\n");
        html.append("        body { font-family: Arial, sans-serif; margin: 20px; background-color: #f5f5f5; }\n");
        html.append(
                "        .container { max-width: 1200px; margin: 0 auto; background-color: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }\n");
        html.append("        h1 { color: #333; border-bottom: 3px solid #007bff; padding-bottom: 10px; }\n");
        html.append(
                "        .summary { display: grid; grid-template-columns: repeat(4, 1fr); gap: 15px; margin-bottom: 30px; }\n");
        html.append(
                "        .stat-box { padding: 20px; border-radius: 8px; color: white; text-align: center; font-weight: bold; font-size: 24px; }\n");
        html.append("        .passed { background-color: #28a745; }\n");
        html.append("        .failed { background-color: #dc3545; }\n");
        html.append("        .skipped { background-color: #ffc107; }\n");
        html.append("        .total { background-color: #007bff; }\n");
        html.append("        .stat-label { font-size: 12px; margin-top: 5px; }\n");
        html.append("        table { width: 100%; border-collapse: collapse; margin-top: 20px; }\n");
        html.append("        th { background-color: #007bff; color: white; padding: 12px; text-align: left; }\n");
        html.append("        td { padding: 10px; border-bottom: 1px solid #ddd; }\n");
        html.append("        tr:hover { background-color: #f9f9f9; }\n");
        html.append("        .PASSED { color: #28a745; font-weight: bold; }\n");
        html.append("        .FAILED { color: #dc3545; font-weight: bold; }\n");
        html.append("        .SKIPPED { color: #ffc107; font-weight: bold; }\n");
        html.append("        .footer { margin-top: 30px; text-align: center; color: #666; font-size: 12px; }\n");
        html.append("    </style>\n");
        html.append("</head>\n");
        html.append("<body>\n");
        html.append("    <div class=\"container\">\n");
        html.append("        <h1>🧪 Test Execution Report</h1>\n");
        html.append("        <div class=\"summary\">\n");
        html.append(String.format(
                "            <div class=\"stat-box total\">%d<div class=\"stat-label\">Total Tests</div></div>\n",
                totalTests));
        html.append(String.format(
                "            <div class=\"stat-box passed\">%d<div class=\"stat-label\">Passed</div></div>\n",
                passedTests));
        html.append(String.format(
                "            <div class=\"stat-box failed\">%d<div class=\"stat-label\">Failed</div></div>\n",
                failedTests));
        html.append(String.format(
                "            <div class=\"stat-box skipped\">%d<div class=\"stat-label\">Skipped</div></div>\n",
                skippedTests));
        html.append("        </div>\n");
        html.append(String.format(
                "        <p><strong>Pass Rate:</strong> %.1f%% | <strong>Total Duration:</strong> %dms | <strong>Execution Time:</strong> %s to %s</p>\n",
                passRate, totalDuration, testExecutionStart, testExecutionEnd));
        html.append("        <table>\n");
        html.append("            <thead>\n");
        html.append("                <tr>\n");
        html.append("                    <th>Test Name</th>\n");
        html.append("                    <th>Status</th>\n");
        html.append("                    <th>Duration (ms)</th>\n");
        html.append("                    <th>Message</th>\n");
        html.append("                </tr>\n");
        html.append("            </thead>\n");
        html.append("            <tbody>\n");

        for (final TestResult result : testResults) {
            html.append("                <tr>\n");
            html.append(String.format("                    <td>%s</td>\n", result.testName));
            html.append(String.format("                    <td class=\"%s\">%s</td>\n", result.status, result.status));
            html.append(String.format("                    <td>%d</td>\n", result.duration));
            html.append(
                    String.format("                    <td>%s</td>\n", result.message != null ? result.message : "-"));
            html.append("                </tr>\n");
        }

        html.append("            </tbody>\n");
        html.append("        </table>\n");
        html.append("        <div class=\"footer\">\n");
        html.append(String.format("            <p>Report generated on %s | demoPlaywright Test Framework</p>\n",
                LocalDateTime.now().format(TIMESTAMP_FORMATTER)));
        html.append("        </div>\n");
        html.append("    </div>\n");
        html.append("</body>\n");
        html.append("</html>\n");

        return html.toString();
    }

    /**
     * Get the reports directory.
     *
     * @return path to reports directory
     */
    public static String getReportsDir() {
        return REPORTS_DIR;
    }

    /**
     * Get total test count.
     *
     * @return total number of recorded tests
     */
    public static int getTotalTests() {
        return testResults.size();
    }

    /**
     * Get passed test count.
     *
     * @return number of passed tests
     */
    public static long getPassedTests() {
        return testResults.stream().filter(r -> "PASSED".equals(r.status)).count();
    }

    /**
     * Get failed test count.
     *
     * @return number of failed tests
     */
    public static long getFailedTests() {
        return testResults.stream().filter(r -> "FAILED".equals(r.status)).count();
    }

    /**
     * Clear all recorded results.
     */
    public static void clearResults() {
        testResults.clear();
        logger.info("Cleared all test results");
    }

    // ==================== Inner Classes ====================

    /**
     * Test result data class.
     */
    private static class TestResult {
        final String testName;
        final String status;
        final long duration;
        final String message;
        final String timestamp;

        TestResult(final String testName, final String status, final long duration, final String message) {
            this.testName = testName;
            this.status = status;
            this.duration = duration;
            this.message = message;
            this.timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        }
    }

    /**
     * Execution summary data class.
     */
    private static class ExecutionSummary {
        final List<TestResult> results;
        final String startTime;
        final String endTime;
        final long totalDuration;
        final long totalTests;
        final long passedTests;
        final long failedTests;
        final long skippedTests;
        final double passRate;

        ExecutionSummary(final List<TestResult> results, final LocalDateTime startTime, final LocalDateTime endTime) {
            this.results = results;
            this.startTime = startTime.format(TIMESTAMP_FORMATTER);
            this.endTime = endTime.format(TIMESTAMP_FORMATTER);
            this.totalDuration = results.stream().mapToLong(r -> r.duration).sum();
            this.totalTests = results.size();
            this.passedTests = results.stream().filter(r -> "PASSED".equals(r.status)).count();
            this.failedTests = results.stream().filter(r -> "FAILED".equals(r.status)).count();
            this.skippedTests = results.stream().filter(r -> "SKIPPED".equals(r.status)).count();
            this.passRate = this.totalTests > 0 ? (this.passedTests * 100.0) / this.totalTests : 0;
        }
    }
}
