package cucumber;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Cucumber test runner for JUnit 5.
 * Executes all feature files from src/test/resources/features/
 */
@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/resources/features", glue = { "cucumber", "steps" }, plugin = {
        "progress",
        "html:target/cucumber-reports/cucumber-report.html",
        "json:target/cucumber-reports/cucumber-report.json"
}, tags = "not @skip", publish = false)
public class CucumberRunnerTest {
    // Cucumber runner test
}
