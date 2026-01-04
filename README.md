# Playwright Automation Framework - Enhanced Edition

A comprehensive Java-based testing framework built on Playwright with advanced features for accessibility testing, network mocking, artifact management, and code quality enforcement.

## Quick Start

### Prerequisites
- Java 17+
- Maven 3.8.0+
- Chrome/Chromium browser

### Clone & Setup

```bash
git clone <repository>
cd demoPlaywright
mvn clean install
```

### Run Tests

```bash
# Run all tests
mvn clean test

# Run specific test class
mvn test -Dtest=LoginTest

# Run with specific browser
mvn test -Dbrowser=firefox

# Run with debugging
mvn test -Dorg.slf4j.simpleLogger.defaultLogLevel=debug
```

---

## Framework Features

### A. Code Quality & Review Capability

#### Enforced Code Style
- **CheckStyle**: Validates naming, structure, formatting
- **PMD**: Detects code quality issues and best practice violations
- **SpotBugs**: Identifies potential bugs and security issues

Run checks:
```bash
mvn checkstyle:check
mvn pmd:check
mvn spotbugs:check
```

#### Code Organization
- **api/**: API client utilities
- **core/**: Framework core (BaseTest, BrowserManager, extensions)
- **utils/**: Reusable helpers (waits, assertions, mocks, fixtures)
- **resources/**: Configuration and logging setup

#### Reusable Helpers

**WaitHelper** - Explicit waits and retries
```java
WaitHelper.waitFor(() -> element.isVisible(), "Element visibility", 5000);
WaitHelper.retry(() -> action.execute(), "Action", 3);
WaitHelper.waitForNavigation(page, () -> button.click(), "Navigation");
```

**AssertionHelper** - Custom assertions
```java
AssertionHelper.assertPageUrl(page, "/login", "TestName");
AssertionHelper.assertElementVisible(element, "Element description", "TestName");
AssertionHelper.assertElementText(element, "Expected text", "TestName");
```

**MockHelper** - Network mocking
```java
MockHelper.mockRoute(page, "*/api/users", responseJson, 200);
MockHelper.mockRouteWithFixture(page, "*/api/users", "user.json", 200);
MockHelper.mockRouteError(page, "*/api/login", 401, "Unauthorized");
```

**FixtureLoader** - Test data fixtures
```java
User user = FixtureLoader.loadFixture("user-response.json", User.class);
String json = FixtureLoader.loadFixtureAsString("api-response.json");
```

---

### B. Accessibility Testing

Complete a11y scanning with Axe-core integration.

```java
// Scan entire page
AccessibilityUtil.scan(page, "HomePage");

// Scan specific element
AccessibilityUtil.scanElement(page, element, "Product card");

// Check keyboard navigation
AccessibilityUtil.checkKeyboardNavigation(page, "HomePage");

// Validate ARIA attributes
AccessibilityUtil.validateAriaAttributes(element, "Email input");

// Whitelist known violations
AccessibilityUtil.whitelistViolation("HomePage", "color-contrast");
```

---

### C. Test Mocking & Fixtures

#### Network Request Mocking

```java
String apiResponse = FixtureLoader.loadFixtureAsString("user-response.json");
MockHelper.mockRoute(page, "*/api/users", apiResponse, 200);

MockHelper.mockRouteError(page, "*/api/login", 500, "Server error");
MockHelper.mockRouteAbort(page, "*/ads/*");
```

#### Test Fixtures
Store JSON fixtures in `src/test/resources/fixtures/`:

```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john@example.com"
}
```

---

### D. Reporting & Artifacts

#### Screenshots
- **Automatic on failure**: Captured when tests fail
- **Full-page option**: Capture entire scrollable page
- **Storage**: `target/test-artifacts/screenshots/`

#### Video Recording
Enable in config.properties:
```properties
video.record=true
video.record.always=false
```

#### Trace Recording
Enable in config.properties:
```properties
trace.record=true
```

View traces:
```bash
npx playwright show-trace target/test-artifacts/traces/test-name.zip
```

#### Artifact Cleanup
```java
ArtifactManager.cleanupOldArtifacts(50);        // Keep 50 most recent
ArtifactManager.cleanupArtifactsByAge(7);       // Keep 7 days
```

---

## Configuration

### config.properties

```properties
browser=chromium
headless=false
base.url=https://google.com
timeout=30000
video.record=false
trace.record=false
artifacts.max.files=50
```

---

## Example Test

```java
@DisplayName("Login Tests")
class LoginTest extends BaseTest {

    @Test
    @DisplayName("Login with valid credentials")
    void loginWithValidCredentials() {
        // Mock API
        MockHelper.mockRoute(getPage(), "*/api/login", "{\"token\":\"abc\"}", 200);

        // Load test data
        User user = FixtureLoader.loadFixture("valid-user.json", User.class);

        // Perform login
        getPage().fill("#email", user.getEmail());
        getPage().fill("#password", user.getPassword());
        getPage().click("#submit");

        // Assert
        WaitHelper.waitFor(() -> getPage().url().contains("/home"), "Navigate to home");
        AssertionHelper.assertPageUrl(getPage(), "/home", "LoginTest");
    }

    @Test
    @DisplayName("Login page accessibility")
    void loginPageAccessibility() {
        AccessibilityUtil.scan(getPage(), "LoginPage");
        AccessibilityUtil.checkKeyboardNavigation(getPage(), "LoginPage");
    }
}
```

---

## Code Quality

```bash
# Pre-commit checks
mvn clean checkstyle:check pmd:check spotbugs:check test

# Code coverage
mvn clean test jacoco:report

# SonarQube analysis
mvn clean test sonar:sonar
```

---

## PR Checklist

- [ ] Code passes CheckStyle, PMD, SpotBugs
- [ ] Tests pass consistently
- [ ] No hard-coded data
- [ ] Proper logging
- [ ] A11y scans for critical pages
- [ ] Meaningful test names
- [ ] No duplicate code

---

## Documentation

- **TESTING.md**: Comprehensive testing guidelines
- **Javadoc**: In-source documentation
- **Example**: See `LoginTest.java`

---

## Useful Commands

```bash
mvn clean test
mvn test -Dtest=LoginTest
mvn checkstyle:check pmd:check spotbugs:check
mvn site
tail -f target/logs/test-execution.log
npx playwright show-trace target/test-artifacts/traces/test-name.zip
```

---

## Support

For questions, see TESTING.md for detailed guidelines and examples.

