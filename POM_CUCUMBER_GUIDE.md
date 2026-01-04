# POM + Cucumber Framework Documentation

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   ├── api/
│   │   │   ├── ApiClient.java
│   │   │   └── ApiUtils.java
│   │   ├── core/
│   │   │   ├── BaseTest.java
│   │   │   ├── BrowserExtension.java
│   │   │   └── BrowserManager.java
│   │   ├── pages/                    # Page Objects (NEW)
│   │   │   ├── LoginPage.java
│   │   │   └── HomePage.java
│   │   └── utils/
│   │       ├── AccessibilityUtil.java
│   │       ├── ArtifactManager.java
│   │       ├── AssertionHelper.java
│   │       ├── ConfigReader.java
│   │       ├── FixtureLoader.java
│   │       ├── MockHelper.java
│   │       ├── TestDataGenerator.java
│   │       └── WaitHelper.java
│   └── resources/
│       ├── config.properties
│       ├── logback.xml
│       └── a11y-whitelist.json
└── test/
    ├── java/
    │   ├── CucumberRunnerTest.java    # Cucumber JUnit Runner (NEW)
    │   ├── cucumber/                  # Cucumber Configuration (NEW)
    │   │   ├── BrowserHooks.java
    │   │   └── ScenarioContext.java
    │   ├── steps/                     # Step Definitions (NEW)
    │   │   ├── ui/
    │   │   │   └── LoginSteps.java
    │   │   └── api/
    │   │       └── APIAuthSteps.java
    │   ├── ui/                        # JUnit Tests (existing)
    │   │   ├── AccessibilityTest.java
    │   │   ├── LoginTest.java
    │   │   └── SmokeTest.java
    │   └── core/
    │       └── BaseTest.java
    └── resources/
        ├── features/                  # Cucumber Feature Files (NEW)
        │   ├── ui/
        │   │   └── login.feature
        │   └── api/
        │       └── auth.feature
        └── fixtures/
            ├── user-response.json
            └── error-response.json
```

## Key Components

### 1. **Page Objects** (`src/main/java/pages/`)
- **LoginPage.java**: Encapsulates all login page interactions
  - Methods: `open()`, `enterEmail()`, `enterPassword()`, `login()`, `verifyErrorMessage()`
  - Uses WaitHelper and AssertionHelper internally
  
- **HomePage.java**: Encapsulates home page interactions
  - Methods: `verifyOnHomePage()`, `getUserName()`, `logout()`, `verifyPageUrl()`

**Benefits**:
- Reusable across JUnit and Cucumber tests
- Centralized locator management
- Easy to maintain when UI changes

### 2. **Cucumber Configuration** (`src/test/java/cucumber/`)

- **ScenarioContext.java**: Thread-local storage for Page and BrowserContext
  - Passes browser instance between hooks and step definitions
  - Ensures test isolation in parallel execution

- **BrowserHooks.java**: Cucumber @Before/@After lifecycle hooks
  - `@Before`: Creates BrowserManager, initializes ArtifactManager, creates new context/page per scenario
  - `@After`: Captures screenshot on failure, closes context, cleans up thread-local storage

### 3. **Step Definitions** (`src/test/java/steps/`)

- **steps/ui/LoginSteps.java**: UI interaction steps
  - Uses `@Given`, `@When`, `@Then` annotations
  - Calls LoginPage methods for interactions
  - Runs accessibility scans via AccessibilityUtil

- **steps/api/APIAuthSteps.java**: API testing steps
  - Demonstrates API request/response handling
  - Can be extended with RestAssured or HttpClient
  - Includes mock responses for demo purposes

### 4. **Feature Files** (`src/test/resources/features/`)

- **features/ui/login.feature**: User login scenarios
  - Scenario 1: Valid login
  - Scenario 2: Invalid credentials
  - Scenario 3: Validation error

- **features/api/auth.feature**: Authentication API scenarios
  - Scenario 1: Valid authentication
  - Scenario 2: Invalid credentials
  - Scenario 3: Missing email validation

### 5. **Cucumber Runner** (`CucumberRunnerTest.java`)
- JUnit-based runner for Cucumber
- Configures:
  - Features path: `src/test/resources/features`
  - Glue (step definitions): `cucumber`, `steps` packages
  - Plugins: HTML report, JSON report
  - Tagging: Excludes @skip tests

## How It Works

### Test Execution Flow

1. **mvn test** → Maven Surefire discovers tests
2. **CucumberRunnerTest** → Cucumber scans feature files
3. **Per Scenario**:
   - `BrowserHooks.beforeScenario()` → Creates browser, page, context
   - `ScenarioContext.set()` → Makes page available to steps
   - **Step execution** → Steps call page objects
   - `BrowserHooks.afterScenario()` → Screenshot on failure, cleanup
4. **Report generation** → HTML and JSON reports in `target/cucumber-reports/`

### Example: Login UI Test Flow

```
Scenario: User logs in with valid credentials
  ↓
@Before hook: Create browser context and page
  ↓
Given I open the login page
  → LoginSteps.openLoginPage()
  → LoginPage.open()
  → AccessibilityUtil.scan()
  ↓
When I enter email "user@example.com"
  → LoginSteps.enterEmail()
  → LoginPage.enterEmail()
  ↓
And I enter password "SecurePassword123"
  → LoginSteps.enterPassword()
  → LoginPage.enterPassword()
  ↓
And I click the login button
  → LoginSteps.clickLoginButton()
  → LoginPage.clickLoginButton()
  ↓
Then I should be redirected to the home page
  → LoginSteps.verifyRedirectedToHome()
  → HomePage.verifyPageUrl()
  → HomePage.verifyOnHomePage()
  ↓
@After hook: Screenshot on failure (if applicable), cleanup
```

## Running Tests

### Run all Cucumber tests
```bash
mvn clean test
```

### Run only UI tests
```bash
mvn clean test -Dcucumber.filter.tags="@ui"
```

### Run only login scenarios
```bash
mvn clean test -Dcucumber.filter.tags="@login"
```

### Run specific feature file
```bash
mvn clean test -Dtest=CucumberRunnerTest
```

### Run with specific tags
```bash
mvn clean test -Dcucumber.filter.tags="@smoke"
```

## Integration with Existing Framework

### Utilities Used by Cucumber Tests
- **WaitHelper**: For explicit waits (used in page objects)
- **AssertionHelper**: For assertions (used in page objects and steps)
- **MockHelper**: For API mocking
- **FixtureLoader**: For test data (JSON fixtures)
- **AccessibilityUtil**: For Axe scans
- **ArtifactManager**: For screenshots on failure (via hooks)
- **ConfigReader**: For configuration properties

### Coexistence with JUnit Tests
- JUnit tests remain unchanged in `src/test/java/ui/`
- Both frameworks can run together: `mvn test`
- Can run only JUnit: `mvn -Dtest=ui.* test`
- Can run only Cucumber: `mvn -Dtest=CucumberRunnerTest test`

## Extending the Framework

### Add a New UI Page Object
1. Create `src/main/java/pages/NewPage.java`
2. Define locators and methods
3. Use WaitHelper and AssertionHelper inside
4. Example:
```java
package pages;

import com.microsoft.playwright.Page;
import utils.WaitHelper;
import utils.AssertionHelper;

public class NewPage {
    private final Page page;
    private final String SOME_LOCATOR = "#id";

    public NewPage(final Page page) {
        this.page = page;
    }

    public void interact() {
        WaitHelper.waitForElement(page.locator(SOME_LOCATOR), "Element");
        page.locator(SOME_LOCATOR).click();
    }
}
```

### Add New Feature File
1. Create `src/test/resources/features/feature-name.feature`
2. Write Gherkin scenarios with tags:
```gherkin
@feature @smoke
Scenario: User does something
  Given I prepare the feature
  When I do something
  Then I should see expected result
```

### Add New Step Definition Class
1. Create `src/test/java/steps/feature/FeatureSteps.java`
2. Use `@Given`, `@When`, `@Then` annotations
3. Call page objects and utilities
4. Make sure package name is under `glue` path in runner

### Add Accessibility Check to a Step
```java
import utils.AccessibilityUtil;

@Given("I open the page")
public void openPage() {
    final Page page = ScenarioContext.getPage();
    page.navigate(url);
    AccessibilityUtil.scan(page, "PageName");
}
```

## Reports

### Cucumber Reports Location
- HTML: `target/cucumber-reports/cucumber-report.html`
- JSON: `target/cucumber-reports/cucumber-report.json`
- Screenshots on failure: `target/test-artifacts/screenshots/`

### View Reports
```bash
# Open HTML report
open target/cucumber-reports/cucumber-report.html  # macOS
start target/cucumber-reports/cucumber-report.html # Windows
xdg-open target/cucumber-reports/cucumber-report.html # Linux
```

## Tags Guide

### Common Tags (use with `-Dcucumber.filter.tags`)
- `@ui` - UI tests
- `@api` - API tests
- `@smoke` - Smoke tests
- `@login` - Login-related tests
- `@accessibility` - Accessibility tests
- `@negative` - Negative test scenarios
- `@validation` - Validation tests
- `@skip` - Skip these tests

### Example Commands
```bash
mvn test -Dcucumber.filter.tags="@ui and @smoke"
mvn test -Dcucumber.filter.tags="@api or @integration"
mvn test -Dcucumber.filter.tags="not @skip"
```

## Debugging

### Enable verbose logging
- Set log level in `src/main/resources/logback.xml`
- Or use: `mvn test -Dlogging.level=DEBUG`

### Run with visible browser
- Update `config.properties`: `headless=false`

### Capture screenshots
- Screenshots on failure: automatic via `BrowserHooks`
- Location: `target/test-artifacts/screenshots/`

## Best Practices

1. **Page Objects**: Keep them focused on single page interactions
2. **Step Definitions**: Keep steps small and reusable (2-3 lines of code)
3. **Gherkin**: Use clear, business-readable language
4. **DRY**: Reuse page object methods in step definitions
5. **Tagging**: Use tags to organize and filter tests
6. **Fixtures**: Store test data in JSON under `src/test/resources/fixtures/`
7. **Accessibility**: Add scans to critical pages via AccessibilityUtil
8. **Logging**: Leverage structured logging for debugging

## Troubleshooting

### Feature file not found
- Ensure files are in `src/test/resources/features/`
- Check file extension: `.feature`
- Check glue path in CucumberRunnerTest

### Steps not recognized
- Verify step class is under `glue` package in runner
- Check @Given, @When, @Then annotations match scenario steps exactly
- Ensure parameter types match (String, int, etc.)

### ScenarioContext returning null
- Verify `@Before` hook runs before steps (check log output)
- Ensure ScenarioContext.set() is called in hook
- Check that glue includes `cucumber` package

### Browser not launching
- Verify `BrowserManager.java` is in core package
- Check `config.properties` has `browser=chromium` and `headless=false/true`
- Verify Playwright dependencies in pom.xml
