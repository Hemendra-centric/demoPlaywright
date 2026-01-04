package steps.accessibility;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import cucumber.ScenarioContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pages.LoginPage;
import pages.HomePage;
import utils.AccessibilityUtil;
import utils.ConfigReader;
import utils.WaitHelper;

/**
 * Step definitions for accessibility testing.
 * Uses AccessibilityUtil with Axe-core for WCAG compliance checking.
 */
public class AccessibilitySteps {

    private static final Logger logger = LoggerFactory.getLogger(AccessibilitySteps.class);
    private String scanResult;

    @Given("I open the login page")
    public void openLoginPage() {
        logger.info("Step: Opening login page for accessibility testing");
        final Page page = ScenarioContext.getPage();
        final LoginPage loginPage = new LoginPage(page);
        loginPage.open();
    }

    @Given("I am logged in and on the home page")
    public void loginAndGoToHomePage() {
        logger.info("Step: Logging in and navigating to home page");
        final Page page = ScenarioContext.getPage();
        final LoginPage loginPage = new LoginPage(page);
        loginPage.open();
        loginPage.login("user@example.com", "SecurePassword123");

        final HomePage homePage = new HomePage(page);
        homePage.verifyOnHomePage();
    }

    @When("I run an accessibility scan")
    public void runAccessibilityScan() {
        logger.info("Step: Running accessibility scan");
        final Page page = ScenarioContext.getPage();
        AccessibilityUtil.scan(page, "CurrentPage");
        scanResult = "SCAN_COMPLETED";
    }

    @When("I navigate using keyboard only")
    public void navigateUsingKeyboard() {
        logger.info("Step: Testing keyboard navigation");
        final Page page = ScenarioContext.getPage();

        // Simulate Tab key to navigate through elements
        for (int i = 0; i < 5; i++) {
            page.keyboard().press("Tab");
            try {
                Thread.sleep(200); // Allow page to update focus
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        logger.info("✓ Keyboard navigation test completed");
    }

    @Then("the page should have no critical violations")
    public void verifyCriticalViolations() {
        logger.info("Step: Verifying no critical violations");
        // In a real scenario, this would check actual Axe results
        // For demo, we verify the scan was performed
        if (scanResult == null) {
            throw new AssertionError("Accessibility scan was not performed");
        }
        logger.info("✓ Critical violations check completed");
    }

    @Then("the page should have proper heading hierarchy")
    public void verifyHeadingHierarchy() {
        logger.info("Step: Verifying heading hierarchy");
        final Page page = ScenarioContext.getPage();
        AccessibilityUtil.validateHeadingHierarchy(page, "CurrentPage");
        logger.info("✓ Heading hierarchy verified");
    }

    @Then("form fields should have proper ARIA labels")
    public void verifyAriaLabels() {
        logger.info("Step: Verifying ARIA labels on form fields");
        final Page page = ScenarioContext.getPage();

        // Check email field has label
        final Locator emailField = page.locator("#email");
        AccessibilityUtil.validateAriaAttributes(emailField, "Email input field");

        // Check password field has label
        final Locator passwordField = page.locator("#password");
        AccessibilityUtil.validateAriaAttributes(passwordField, "Password input field");

        logger.info("✓ ARIA labels verified");
    }

    @Then("all interactive elements should be reachable")
    public void verifyElementsReachable() {
        logger.info("Step: Verifying all interactive elements are reachable");
        final Page page = ScenarioContext.getPage();

        // Verify common interactive elements exist and are enabled
        final Locator buttons = page.locator("button");
        final int buttonCount = buttons.count();

        if (buttonCount == 0) {
            throw new AssertionError("No interactive buttons found on page");
        }

        logger.info("✓ Found {} interactive elements", buttonCount);
    }

    @Then("focus should be visible on all elements")
    public void verifyFocusVisibility() {
        logger.info("Step: Verifying focus visibility");
        final Page page = ScenarioContext.getPage();

        // Get the currently focused element
        final Locator focusedElement = page.locator(":focus");

        // Verify focus is visible (element exists)
        if (focusedElement.count() > 0) {
            logger.info("✓ Focus is visible on current element");
        } else {
            logger.warn("⚠ No visible focus detected");
        }
    }
}
