package steps.ui;

import com.microsoft.playwright.Page;
import cucumber.ScenarioContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pages.HomePage;
import pages.LoginPage;
import utils.AccessibilityUtil;
import utils.ConfigReader;

/**
 * Step definitions for login feature.
 * Uses page objects to interact with the login and home pages.
 */
public class LoginSteps {

    private static final Logger logger = LoggerFactory.getLogger(LoginSteps.class);
    private LoginPage loginPage;
    private HomePage homePage;

    @Given("I open the login page")
    public void openLoginPage() {
        logger.info("Step: Opening login page");
        final Page page = ScenarioContext.getPage();
        loginPage = new LoginPage(page);
        loginPage.open();

        // Run accessibility scan on login page
        AccessibilityUtil.scan(page, "LoginPage");
    }

    @When("I enter email {string}")
    public void enterEmail(final String email) {
        logger.info("Step: Entering email: {}", email);
        loginPage.enterEmail(email);
    }

    @When("I enter password {string}")
    public void enterPassword(final String password) {
        logger.info("Step: Entering password");
        loginPage.enterPassword(password);
    }

    @When("I click the login button")
    public void clickLoginButton() {
        logger.info("Step: Clicking login button");
        loginPage.clickLoginButton();
    }

    @Then("I should be redirected to the home page")
    public void verifyRedirectedToHome() {
        logger.info("Step: Verifying redirection to home page");
        final Page page = ScenarioContext.getPage();
        homePage = new HomePage(page);

        // Wait for home page to load
        homePage.verifyPageUrl("/home");
        homePage.verifyOnHomePage();

        logger.info("✓ User successfully logged in and redirected to home page");
    }

    @Then("I should see error message {string}")
    public void verifyErrorMessage(final String expectedMessage) {
        logger.info("Step: Verifying error message: {}", expectedMessage);
        if (loginPage == null) {
            loginPage = new LoginPage(ScenarioContext.getPage());
        }
        loginPage.verifyErrorMessage(expectedMessage);
    }
}
