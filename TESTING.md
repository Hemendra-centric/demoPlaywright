# Testing Guidelines & PR Checklist

## Overview
This document provides guidelines for writing maintainable, reliable, and reusable tests in the Playwright automation framework.

---

## Part A: Code Review Capability

### 1. Code Style & Formatting

**Requirements:**
- All code must pass CheckStyle validation (run `mvn checkstyle:check`)
- Follow Java naming conventions:
  - Classes: `PascalCase` (e.g., `LoginTest`, `AccessibilityUtil`)
  - Methods: `camelCase` (e.g., `loginWithValidCredentials()`)
  - Constants: `UPPER_SNAKE_CASE` (e.g., `DEFAULT_TIMEOUT`)
  - Variables: `camelCase` (e.g., `userName`, `isVisible`)

**CheckStyle Rules Enforced:**
- Line length: max 120 characters
- Method length: max 150 lines
- Parameter count: max 7 per method
- No wildcard imports
- No trailing whitespace
- Proper brace placement

**Verification:**
```bash
mvn checkstyle:check
mvn pmd:check
mvn spotbugs:check
```

---

### 2. Project Structure

**Directory Layout:**
```
src/
├── main/
│   ├── java/
│   │   ├── api/              # API client utilities
│   │   ├── core/             # Framework core (BaseTest, BrowserManager, etc.)
│   │   └── utils/            # Helper utilities (waits, assertions, mocks, etc.)
│   └── resources/
│       ├── config.properties # Configuration
│       └── logback.xml       # Logging configuration
└── test/
    ├── java/
    │   ├── core/             # Test base classes
    │   └── ui/               # UI/E2E tests
    └── resources/
        ├── fixtures/         # JSON fixtures for mocking
        └── a11y-whitelist.json # A11y violation whitelist
```

---

### 3. Reusable Helpers

**Available Utilities:**

#### WaitHelper
Explicit waits for stable tests:
```java
WaitHelper.waitFor(() -> element.isVisible(), "Submit button visibility");
WaitHelper.retry(() -> api.fetchUser(), "API call", 3);
WaitHelper.waitForNavigation(page, () -> button.click(), "Navigation to login page");
```

#### AssertionHelper
Custom assertions with better error messages:
```java
AssertionHelper.assertPageUrl(page, "/login", "LoginTest");
AssertionHelper.assertElementVisible(loginForm, "Login form", "LoginTest");
AssertionHelper.assertElementText(button, "Submit", "LoginTest");
AssertionHelper.assertElementEnabled(submitBtn, "Submit button", "LoginTest");
```

#### MockHelper
Network request mocking:
```java
MockHelper.mockRoute(page, "*/api/users", responseJson, 200);
MockHelper.mockRouteWithFixture(page, "*/api/users", "user-response.json", 200);
MockHelper.mockRouteError(page, "*/api/users", 500, "Server error");
MockHelper.mockRouteAbort(page, "*/ads/*");  // Block ads
```

#### FixtureLoader
Load test data from JSON fixtures:
```java
User user = FixtureLoader.loadFixture("user-response.json", User.class);
String errorResponse = FixtureLoader.loadFixtureAsString("error-response.json");
```

#### ArtifactManager
Screenshots, videos, and traces:
```java
ArtifactManager.takeScreenshot(page, "test-name", true);  // fullPage=true
ArtifactManager.cleanupOldArtifacts(50);  // Keep only 50 most recent
```

---

### 4. No Hard-Coded Data

**INCORRECT:**
```java
page.fill("#email", "test@example.com");
page.fill("#password", "hardcodedPassword123!");
```

**CORRECT - Use fixtures:**
```java
User testUser = FixtureLoader.loadFixture("valid-user.json", User.class);
page.fill("#email", testUser.getEmail());
page.fill("#password", testUser.getPassword());
```

**CORRECT - Use generators:**
```java
String randomEmail = TestDataGenerator.randomEmail();
String randomPassword = TestDataGenerator.randomPassword();
```

**CORRECT - Use config:**
```java
String baseUrl = ConfigReader.getBaseUrl();
int timeout = ConfigReader.getInt("timeout");
```

---

### 5. Exception Handling

**INCORRECT - Silent failures:**
```java
try {
    element.click();
} catch (Exception e) {
    // Silently ignore
}
```

**CORRECT - Explicit logging and handling:**
```java
try {
    element.click();
} catch (TimeoutError e) {
    logger.error("Element not clickable within timeout", e);
    ArtifactManager.takeScreenshot(page, testName, true);
    throw new AssertionError("Failed to click element", e);
}
```

---

### 6. Logging Strategy

**Log Levels:**
- **ERROR**: Test failures, critical issues
- **WARN**: Non-critical issues, whitelisted violations
- **INFO**: Test milestones, significant actions
- **DEBUG**: Detailed test execution flow
- **TRACE**: Framework internals

**Examples:**
```java
logger.error("Test failed: {}", testName);
logger.warn("Non-critical assertion failed");
logger.info("✓ Login successful");
logger.debug("Navigating to: {}", url);
logger.trace("Setting locator: {}", selector);
```

**View Logs:**
```bash
# All logs
tail -f target/logs/test-execution.log

# Errors only
tail -f target/logs/test-execution-error.log

# Specific test
grep "TestName" target/logs/test-execution.log
```

---

### 7. Meaningful Naming

**Test Names:**
```java
// GOOD: Describes what is being tested and expected outcome
@Test
void loginWithValidCredentials_shouldNavigateToHomepage() {
    // Test implementation
}

@Test
void submitFormWithMissingEmail_shouldShowValidationError() {
    // Test implementation
}

// BAD: Non-descriptive names
@Test
void test1() { }

@Test
void login() { }
```

**Method Names:**
```java
// GOOD: Clear action
private void fillLoginForm(String email, String password) { }
private void verifyErrorMessage(String expectedError) { }
private void navigateTo(String url) { }

// BAD: Vague
private void doLogin() { }
private void check() { }
```

---

### 8. PR Checklist

Before submitting a pull request, verify:

- [ ] **Code Quality**
  - [ ] `mvn checkstyle:check` passes
  - [ ] `mvn pmd:check` passes
  - [ ] `mvn spotbugs:check` passes
  - [ ] No commented-out code
  - [ ] No debug statements (System.out.println)

- [ ] **Test Stability**
  - [ ] Tests pass consistently (run 3+ times)
  - [ ] No hard-coded sleeps (use explicit waits)
  - [ ] Proper waits/retries for async actions
  - [ ] Tests are isolated (no dependencies between tests)

- [ ] **Test Readability**
  - [ ] Test name clearly describes scenario
  - [ ] Method names are descriptive
  - [ ] Proper logging in place
  - [ ] Comments explain "why", not "what"

- [ ] **Reusability**
  - [ ] Common functionality extracted to helpers
  - [ ] No duplicate code across tests
  - [ ] Fixtures used for test data
  - [ ] Locators centralized (Page Objects recommended)

- [ ] **Error Handling**
  - [ ] Proper exception handling (no silent failures)
  - [ ] Screenshots captured on failure
  - [ ] Meaningful error messages

- [ ] **Documentation**
  - [ ] Method JavaDoc for public methods
  - [ ] Complex logic commented
  - [ ] README updated if needed

---

## Part B: Accessibility Testing

### 1. Axe-Core Integration

**Basic Scan:**
```java
AccessibilityUtil.scan(page, "HomePage");
```

**Scan with Specific WCAG Tags:**
```java
AccessibilityUtil.scan(page, "LoginPage", List.of("wcag2aa", "wcag21aa"));
```

**Scan Specific Element:**
```java
AccessibilityUtil.scanElement(page, page.locator(".card"), "Product card");
```

### 2. Violation Handling

**Whitelist Known Violations:**
```java
@BeforeEach
void setupWhitelist() {
    AccessibilityUtil.whitelistViolation("HomePage", "color-contrast");
    AccessibilityUtil.whitelistViolation("HomePage", "label");
}
```

**Load Whitelist from File:**
```java
AccessibilityUtil.loadWhitelist("src/test/resources/a11y-whitelist.json");
```

### 3. Additional Checks

**Keyboard Navigation:**
```java
AccessibilityUtil.checkKeyboardNavigation(page, "HomePage");
```

**ARIA Attributes:**
```java
AccessibilityUtil.validateAriaAttributes(submitButton, "Submit button");
```

**Landmarks & Structure:**
```java
AccessibilityUtil.validateLandmarks(page, "HomePage");
```

### 4. A11y Reports

Reports are automatically generated in `target/a11y-reports/`:
- JSON reports with detailed violation information
- Timestamps included in filenames
- Can be reviewed for trend analysis

---

## Part C: Mock/Fake Testing

### 1. Network Request Mocking

**Mock Successful Response:**
```java
String userResponse = FixtureLoader.loadFixtureAsString("user-response.json");
MockHelper.mockRoute(page, "*/api/users", userResponse, 200);
```

**Mock Error Response:**
```java
MockHelper.mockRouteError(page, "*/api/login", 401, "Unauthorized");
```

**Mock Network Failure:**
```java
MockHelper.mockRouteAbort(page, "*/api/users");
```

### 2. Configuration-Based Toggle

**In config.properties:**
```properties
enable.mocking=true
```

**In test:**
```java
if (ConfigReader.getBoolean("enable.mocking")) {
    MockHelper.mockRoute(page, "*/api/users", fixture, 200);
}
```

### 3. Fixture-Based Testing

**Load fixtures:**
```java
User validUser = FixtureLoader.loadFixture("valid-user.json", User.class);
User invalidUser = FixtureLoader.loadFixture("invalid-user.json", User.class);
```

**Use in tests:**
```java
@Test
void loginWithValidUser() {
    User user = FixtureLoader.loadFixture("valid-user.json", User.class);
    // Test implementation
}
```

---

## Part D: Reporting

### 1. Automatic Screenshots

**On Failure:**
Screenshots are automatically captured when tests fail. Find them in:
```
target/test-artifacts/screenshots/
```

**Manual Screenshot:**
```java
ArtifactManager.takeScreenshot(page, "checkpoint-name", true);  // true = full page
```

### 2. Video Recording

**Enable in config.properties:**
```properties
video.record=true
video.record.always=false  # Only record failures by default
```

**Videos saved to:**
```
target/test-artifacts/videos/
```

### 3. Trace Recording

**Enable in config.properties:**
```properties
trace.record=true
```

**View traces:**
```bash
npx playwright show-trace target/test-artifacts/traces/test-name-trace.zip
```

### 4. Test Metadata

Available in logs and reports:
- Test name
- Browser type
- Test start/end time
- Pass/fail status
- Artifacts location

### 5. Artifact Cleanup

**Automatic cleanup after all tests:**
```java
// Keeps only 50 most recent files per directory
ArtifactManager.cleanupOldArtifacts(50);

// Or cleanup by age
ArtifactManager.cleanupArtifactsByAge(7);  // Keep 7 days
```

---

## Best Practices Summary

1. **Write Clear Tests**: Use descriptive names, proper logging
2. **Use Helpers**: Leverage WaitHelper, AssertionHelper, MockHelper
3. **Manage Data**: Load from fixtures, not hard-coded
4. **Handle Errors**: Log properly, capture artifacts
5. **Keep It DRY**: Extract common code to utilities
6. **Test Isolation**: Each test should be independent
7. **Accessibility**: Scan all critical pages
8. **Mock External**: Use fixtures for APIs, not real endpoints
9. **Verify Quality**: Run CheckStyle, PMD, SpotBugs before PR
10. **Document**: Add JavaDoc and meaningful comments

---

## Useful Commands

```bash
# Run all tests
mvn clean test

# Run specific test
mvn test -Dtest=LoginTest

# Run with debug logging
mvn test -Dorg.slf4j.simpleLogger.defaultLogLevel=debug

# Code quality checks
mvn checkstyle:check
mvn pmd:check
mvn spotbugs:check

# View reports
open target/site/checkstyle.html
open target/site/pmd.html

# View logs
tail -f target/logs/test-execution.log

# View trace
npx playwright show-trace target/test-artifacts/traces/test-name.zip
```

---

## Questions?

Refer to the framework utilities documentation in the source code or contact the QA team.
