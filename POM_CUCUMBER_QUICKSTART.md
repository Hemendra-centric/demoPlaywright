# Quick Start: POM + Cucumber Framework

## What Was Added

### 1. **Page Objects** (Reusable UI interaction layers)
- `src/main/java/pages/LoginPage.java` - Login page interactions
- `src/main/java/pages/HomePage.java` - Home page interactions

### 2. **Cucumber Configuration**
- `src/test/java/cucumber/ScenarioContext.java` - Shared browser context
- `src/test/java/cucumber/BrowserHooks.java` - Lifecycle hooks (@Before/@After)

### 3. **Gherkin Feature Files** (Business-readable test scenarios)
- `src/test/resources/features/ui/login.feature` - UI login scenarios (3 scenarios)
- `src/test/resources/features/api/auth.feature` - API auth scenarios (3 scenarios)

### 4. **Step Definitions** (Maps Gherkin to code)
- `src/test/java/steps/ui/LoginSteps.java` - UI steps for login feature
- `src/test/java/steps/api/APIAuthSteps.java` - API steps for auth feature

### 5. **Test Runner**
- `src/test/java/CucumberRunnerTest.java` - JUnit-based Cucumber runner

## Project Structure After Changes

```
demoPlaywright/
├── src/
│   ├── main/java/
│   │   ├── pages/                    ← NEW: Page Objects
│   │   │   ├── LoginPage.java
│   │   │   └── HomePage.java
│   │   ├── core/
│   │   ├── utils/
│   │   └── api/
│   └── test/
│       ├── java/
│       │   ├── CucumberRunnerTest.java   ← NEW: Cucumber Runner
│       │   ├── cucumber/                 ← NEW: Cucumber Setup
│       │   │   ├── ScenarioContext.java
│       │   │   └── BrowserHooks.java
│       │   ├── steps/                    ← NEW: Step Definitions
│       │   │   ├── ui/
│       │   │   │   └── LoginSteps.java
│       │   │   └── api/
│       │   │       └── APIAuthSteps.java
│       │   └── ui/                       (existing JUnit tests)
│       └── resources/
│           ├── features/                 ← NEW: Gherkin Scenarios
│           │   ├── ui/
│           │   │   └── login.feature
│           │   └── api/
│           │       └── auth.feature
│           └── fixtures/
├── POM_CUCUMBER_GUIDE.md           ← Comprehensive documentation
└── SETUP_CUCUMBER_POM.md           ← Setup instructions
```

## Next Steps

### Step 1: Update pom.xml
Add Cucumber dependencies (see `SETUP_CUCUMBER_POM.md`). The dependencies are:
- `cucumber-java` v7.14.0
- `cucumber-junit` v7.14.0
- `cucumber-picocontainer` v7.14.0

### Step 2: Compile
```bash
mvn clean compile
```

### Step 3: Run Cucumber Tests
```bash
# Run all Cucumber tests
mvn test -Dtest=CucumberRunnerTest

# Run only UI tests
mvn test -Dtest=CucumberRunnerTest -Dcucumber.filter.tags="@ui"

# Run only API tests
mvn test -Dtest=CucumberRunnerTest -Dcucumber.filter.tags="@api"

# Run smoke tests
mvn test -Dtest=CucumberRunnerTest -Dcucumber.filter.tags="@smoke"
```

## How It Works (Simple Flow)

```
1. Run tests
   ↓
2. CucumberRunnerTest discovers feature files (login.feature, auth.feature)
   ↓
3. For each Scenario:
   a. BrowserHooks.@Before() runs
      → Creates browser context, page, injects via ScenarioContext
   ↓
   b. Step executes (e.g., "Given I open the login page")
      → LoginSteps.openLoginPage() calls
      → LoginPage.open() (page object method)
   ↓
   c. Next step executes (e.g., "When I enter email")
      → LoginSteps.enterEmail() calls
      → LoginPage.enterEmail() (page object method)
   ↓
   d. Assertion step (e.g., "Then I should be redirected")
      → LoginSteps.verifyRedirectedToHome() calls
      → HomePage.verifyPageUrl() and verifyOnHomePage()
   ↓
   e. BrowserHooks.@After() runs
      → Captures screenshot on failure
      → Closes browser context
      → Cleans up ScenarioContext
```

## Key Features

### Page Objects (Reusable UI Logic)
```java
// Page objects encapsulate UI interactions
LoginPage loginPage = new LoginPage(page);
loginPage.login("user@example.com", "password");
loginPage.verifyErrorMessage("Invalid credentials");
```

### Step Definitions (Readable Gherkin Steps)
```gherkin
# Feature file (business-readable)
Scenario: User logs in with valid credentials
  Given I open the login page
  When I enter email "user@example.com"
  And I enter password "SecurePassword123"
  Then I should be redirected to the home page
```

```java
// Step definitions (map to code)
@Given("I open the login page")
public void openLoginPage() {
    loginPage = new LoginPage(ScenarioContext.getPage());
    loginPage.open();
}
```

### Shared Context (Browser Management)
```java
// Browser is managed by BrowserHooks
// Steps access it via ScenarioContext
Page page = ScenarioContext.getPage();
page.navigate("https://example.com");
```

## File Locations Reference

| Component | Location |
|-----------|----------|
| Login Page Object | `src/main/java/pages/LoginPage.java` |
| Home Page Object | `src/main/java/pages/HomePage.java` |
| Scenario Context | `src/test/java/cucumber/ScenarioContext.java` |
| Browser Hooks | `src/test/java/cucumber/BrowserHooks.java` |
| Login Steps | `src/test/java/steps/ui/LoginSteps.java` |
| Auth Steps | `src/test/java/steps/api/APIAuthSteps.java` |
| Login Feature | `src/test/resources/features/ui/login.feature` |
| Auth Feature | `src/test/resources/features/api/auth.feature` |
| Cucumber Runner | `src/test/java/CucumberRunnerTest.java` |

## Existing Utilities Still Available

All existing utilities work with both JUnit and Cucumber:
- `WaitHelper` - Explicit waits
- `AssertionHelper` - Custom assertions
- `MockHelper` - API mocking
- `FixtureLoader` - JSON fixture loading
- `AccessibilityUtil` - Axe accessibility scans
- `ArtifactManager` - Screenshots on failure
- `ConfigReader` - Configuration management

## Examples Included

### UI Test Example (login.feature)
- 3 scenarios: valid login, invalid credentials, validation error
- Uses LoginPage and HomePage page objects
- Includes accessibility scan

### API Test Example (auth.feature)
- 3 scenarios: valid auth, invalid credentials, missing email
- Demonstrates request/response handling
- Shows error message validation

## Folder Organization

```
features/
├── ui/          ← UI tests (Login, Dashboard, Profile, etc.)
│   └── login.feature
├── api/         ← API tests (Auth, Users, Products, etc.)
│   └── auth.feature
└── accessibility/  ← Accessibility tests (optional)

steps/
├── ui/          ← UI step definitions
│   └── LoginSteps.java
└── api/         ← API step definitions
    └── APIAuthSteps.java

pages/          ← Page Objects
├── LoginPage.java
├── HomePage.java
└── ...OtherPages...
```

## Running Tests

```bash
# All tests (JUnit + Cucumber)
mvn clean test

# Only JUnit tests
mvn -Dtest=ui.* test

# Only Cucumber tests
mvn -Dtest=CucumberRunnerTest test

# Specific Cucumber tags
mvn -Dtest=CucumberRunnerTest test -Dcucumber.filter.tags="@smoke"
mvn -Dtest=CucumberRunnerTest test -Dcucumber.filter.tags="@ui and @login"

# Parallel execution
mvn clean test -T 4   # 4 threads
```

## Reports

After running tests:
- **Cucumber HTML Report**: `target/cucumber-reports/cucumber-report.html`
- **Cucumber JSON Report**: `target/cucumber-reports/cucumber-report.json`
- **Screenshots on Failure**: `target/test-artifacts/screenshots/`
- **Test Logs**: `logs/application.log`

## Coexistence with Existing Framework

- Existing JUnit tests in `src/test/java/ui/` remain unchanged
- Cucumber tests and JUnit tests run separately
- Both use the same utilities (WaitHelper, AssertionHelper, etc.)
- Both use the same BrowserManager and configuration

## What's Next?

1. Add Cucumber dependencies to pom.xml
2. Run `mvn clean compile` to verify compilation
3. Run `mvn test -Dtest=CucumberRunnerTest` to execute Cucumber tests
4. View HTML report in `target/cucumber-reports/cucumber-report.html`
5. Add more feature files and page objects for your specific scenarios

For detailed documentation, see `POM_CUCUMBER_GUIDE.md`
