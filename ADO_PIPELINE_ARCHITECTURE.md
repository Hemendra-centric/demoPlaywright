# Azure DevOps Pipeline Architecture & Flow

## Complete Pipeline Structure

```
┌─────────────────────────────────────────────────────────────────┐
│                    AZURE DEVOPS PIPELINE                        │
│               (Triggered by push or PR to main/develop)          │
└─────────────────────────────────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────────┐
│                    POOL: ubuntu-latest                           │
│  ├─ Java 17 (auto-installed)                                    │
│  ├─ Maven 3.9.0 (auto-installed)                               │
│  └─ Build Agent on Microsoft servers (not your machine)        │
└─────────────────────────────────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────────┐
│                STAGE 1: Build_and_Test                          │
│                    JOB: BuildJob                                │
└─────────────────────────────────────────────────────────────────┘
        │
        ├──► Checkout source code (git clone)
        │        │
        │        └──► RESULT: Source code on agent
        │
        ├──► Build Project (mvn clean compile -DskipTests)
        │        │
        │        └──► RESULT: Compiled .class files in target/
        │
        ├──► Checkstyle Code Review (mvn checkstyle:check)
        │    ├─ Checks coding style
        │    ├─ Verifies naming conventions
        │    └─ Result: PASS/WARN (non-blocking)
        │
        ├──► PMD Analysis (mvn pmd:check)
        │    ├─ Detects code smells
        │    ├─ Finds potential bugs
        │    └─ Result: PASS/WARN (non-blocking)
        │
        ├──► SpotBugs Analysis (mvn spotbugs:check)
        │    ├─ Finds security issues
        │    ├─ Detects bugs
        │    └─ Result: PASS/WARN (non-blocking)
        │
        ├──► Run Unit Tests (mvn test)
        │    ├─ Executes all @Test methods
        │    ├─ Generates: target/surefire-reports/*.xml
        │    └─ Result: May fail (expected if test data missing)
        │
        ├──► Generate Test Reports (mvn verify)
        │    ├─ Runs integration tests
        │    ├─ Generates reports
        │    └─ Result: Reports in target/
        │
        ├──► Generate JaCoCo Code Coverage (mvn jacoco:report)
        │    ├─ Measures code coverage %
        │    ├─ Generates: target/site/jacoco/jacoco.xml
        │    └─ Result: HTML report with coverage metrics
        │
        ├──► Publish JUnit Test Results
        │    ├─ Task: PublishTestResults@2
        │    ├─ Reads: target/surefire-reports/*.xml
        │    ├─ Publishes to: Azure DevOps Tests tab
        │    └─ Result: Visible in Pipelines > Tests
        │
        ├──► Publish JaCoCo Coverage
        │    ├─ Task: PublishCodeCoverageResults@1
        │    ├─ Reads: target/site/jacoco/jacoco.xml
        │    ├─ Publishes to: Azure DevOps Summary
        │    └─ Result: Coverage % visible in pipeline
        │
        ├──► Publish Cucumber Report
        │    ├─ Task: PublishBuildArtifacts@1
        │    ├─ Source: target/cucumber-reports/
        │    ├─ Name: Cucumber-Report
        │    └─ Result: Downloadable artifact
        │
        ├──► Publish Screenshots
        │    ├─ Task: PublishBuildArtifacts@1
        │    ├─ Source: target/test-artifacts/screenshots/
        │    ├─ Name: Test-Screenshots
        │    └─ Result: Downloadable artifact
        │
        ├──► Publish Test Videos
        │    ├─ Task: PublishBuildArtifacts@1
        │    ├─ Source: target/test-artifacts/videos/
        │    ├─ Name: Test-Videos
        │    └─ Result: Video recordings of failures
        │
        ├──► Publish JaCoCo Report
        │    ├─ Task: PublishBuildArtifacts@1
        │    ├─ Source: target/site/jacoco/
        │    ├─ Name: JaCoCo-Report
        │    └─ Result: HTML coverage report
        │
        └──► Publish Accessibility Reports
             ├─ Task: PublishBuildArtifacts@1
             ├─ Source: target/a11y-reports/
             ├─ Name: Accessibility-Reports
             └─ Result: A11y test results
                        │
                        ▼
                (All Tasks Continue on Error: true)
                        │
        ┌───────────────┴───────────────┐
        │                               │
     PASS                            FAIL
        │                               │
        ▼                               ▼
        └─────────────────┬─────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────────┐
│                STAGE 2: Reports_and_Summary                     │
│           (Only if Build_and_Test SUCCEEDED)                    │
│                    JOB: PublishReports                          │
└─────────────────────────────────────────────────────────────────┘
        │
        └──► Display Build Summary
             ├─ Build Number
             ├─ Source Branch
             └─ Status: SUCCESS
                        │
                        ▼
┌─────────────────────────────────────────────────────────────────┐
│                    STAGE 3: Cleanup                             │
│            (ALWAYS runs, even if previous failed)               │
│                    JOB: CleanupJob                              │
└─────────────────────────────────────────────────────────────────┘
        │
        └──► Clean Workspace
             ├─ Remove target/ directory
             ├─ Remove temporary files
             └─ Prepare agent for next run
                        │
                        ▼
┌─────────────────────────────────────────────────────────────────┐
│                  PIPELINE COMPLETE                              │
│               Time: ~5-10 minutes                               │
│             Status: PASSED or FAILED                            │
│          Artifacts: Available in Azure DevOps                   │
│          Results: Visible in Tests tab + Summary               │
└─────────────────────────────────────────────────────────────────┘
                        │
                        ▼
                  USER NOTIFICATION
              ├─ Email notification sent
              ├─ Status: Success or Failure
              ├─ Link to pipeline run
              └─ Artifacts ready to download
```

---

## Pipeline Triggers

```
┌─────────────────────────┐
│   Code Push Event       │
└──────────┬──────────────┘
           │
           ├─ Branch: main          ──►  TRIGGER PIPELINE
           ├─ Branch: develop       ──►  TRIGGER PIPELINE
           ├─ Branch: hemendra      ──►  TRIGGER PIPELINE
           └─ Other branches        ──►  IGNORE
           
           
┌─────────────────────────┐
│   Pull Request Event    │
└──────────┬──────────────┘
           │
           ├─ Target: main          ──►  TRIGGER PIPELINE
           ├─ Target: develop       ──►  TRIGGER PIPELINE
           └─ Target: other         ──►  IGNORE


┌─────────────────────────┐
│  Manual Trigger         │
└──────────┬──────────────┘
           │
           └─ Click "Run Pipeline" in Azure DevOps  ──►  TRIGGER
```

---

## Data Flow

```
┌─────────────┐
│  Source Repo│ (GitHub/GitLab/Azure Repos)
└──────┬──────┘
       │
       │ git clone (Step: Checkout)
       │
       ▼
┌──────────────────────┐
│  Build Agent         │ (ubuntu-latest on Microsoft servers)
│  ├─ /workspace/      │ (cloned code)
│  ├─ /target/         │ (build output)
│  └─ Java 17, Maven   │ (pre-installed)
└──────┬───────────────┘
       │
       ├─ Compile
       │    └─► target/classes/
       │
       ├─ Run Tests
       │    └─► target/surefire-reports/*.xml
       │
       ├─ Generate Reports
       │    └─► target/site/
       │        └─ jacoco/jacoco.xml
       │        └─ jacoco/index.html
       │
       ├─ Code Quality
       │    └─► target/checkstyle-result.xml
       │    └─► target/pmd.xml
       │    └─► target/spotbugsXml.xml
       │
       ├─ Screenshots/Videos
       │    └─► target/test-artifacts/
       │
       └─ Accessibility Reports
            └─► target/a11y-reports/
       │
       ▼
┌──────────────────────┐
│   Azure DevOps       │ (Cloud Storage)
│   ├─ Artifacts       │ (Cucumber, JaCoCo, etc.)
│   ├─ Test Results    │ (JUnit, Cucumber)
│   ├─ Code Coverage   │ (JaCoCo %)
│   ├─ Logs            │ (Full pipeline output)
│   └─ Notifications   │ (Email to team)
└──────────────────────┘
       │
       ▼
┌──────────────────────┐
│   User/Team Access   │
│   ├─ Web Browser     │ (dev.azure.com)
│   ├─ Email           │ (Notifications)
│   └─ API             │ (Programmatic access)
└──────────────────────┘
```

---

## Environment Details

```
┌─────────────────────────────────────────────────────┐
│         Build Agent Environment                     │
└─────────────────────────────────────────────────────┘

vmImage: ubuntu-latest
│
├─ Operating System
│  └─ Linux (Ubuntu LTS)
│
├─ Java
│  ├─ Version: 17
│  ├─ Vendor: OpenJDK
│  ├─ Installation: Auto (Maven@3 task)
│  └─ JAVA_HOME: $(Agent.ToolsDirectory)/Java/openjdk_17/x64
│
├─ Maven
│  ├─ Version: 3.9.0
│  ├─ Installation: Auto (Maven@3 task)
│  └─ MAVEN_HOME: /usr/share/maven (standard location)
│
├─ Git
│  ├─ Pre-installed
│  └─ Used for code checkout
│
├─ Disk Space
│  ├─ Large (sufficient for builds)
│  └─ Cleaned after each run
│
└─ Network
   ├─ Internet access (for Maven dependencies)
   ├─ No firewall restrictions
   └─ Can access Maven Central Repository
```

---

## Artifact Storage & Access

```
┌──────────────────────────────────────────┐
│       Pipeline Artifacts                 │
│      (Automatically Published)            │
└──────────────────────────────────────────┘
                    │
        ┌───────────┼───────────┐
        │           │           │
        ▼           ▼           ▼
┌──────────┐  ┌──────────┐  ┌──────────┐
│ Cucumber │  │ JaCoCo   │  │Screen    │
│ Report   │  │ Report   │  │shots     │
└──────────┘  └──────────┘  └──────────┘
        │           │           │
        ▼           ▼           ▼
    HTML File    HTML Report   Images
    & JSON                     (PNG/JPG)
        │
        └─────────────────┬──────────────┐
                          │              │
                          ▼              ▼
                   Azure DevOps    Download
                   Web UI           from UI
                   │
                   └─ Artifacts Tab
                   └─ Summary Tab
                   └─ Tests Tab
```

---

## Comparison: Execution Paths

### Before Migration (Jenkins)
```
Your Machine
    │
    ├─ Jenkins Server
    │  ├─ Install & Config (manual)
    │  ├─ Setup agents (manual)
    │  ├─ Install plugins (manual)
    │  └─ Maintain & patch (manual)
    │
    ├─ Execute Build
    │  └─ On your server (or remote agents)
    │
    └─ Store Results
       ├─ Jenkins artifact server
       ├─ Email notifications
       └─ Manual report generation
```

### After Migration (Azure DevOps)
```
Your Machine
    │
    └─ Push code to git
       │
       ▼
┌──────────────────┐
│ Azure DevOps     │ (Cloud)
│                  │
├─ Automatically   │
│  detects push    │
│                  │
├─ Triggers        │
│  pipeline        │
│                  │
├─ Creates agent   │
│  (ubuntu-latest) │
│                  │
├─ Installs Java   │
│  & Maven         │
│                  │
├─ Runs build      │
│  (no your        │
│   involvement)   │
│                  │
├─ Publishes       │
│  artifacts       │
│                  │
└─ Sends email     │
   notification    │
       │
       ▼
    You see results
    in Azure DevOps UI
    (nothing on your machine)
```

---

## Performance Metrics

```
Pipeline Execution Timeline (Typical)
┌───────────────────────────────────────────────┐

0-5 seconds
    ├─ Start agent
    └─ Allocate resources

5-10 seconds
    ├─ Checkout code
    ├─ Install Java
    └─ Install Maven

10-30 seconds
    ├─ mvn clean compile
    └─ Code compilation

30-45 seconds
    ├─ mvn checkstyle:check
    ├─ mvn pmd:check
    └─ mvn spotbugs:check

45-120 seconds
    ├─ mvn test
    └─ Run unit tests

120-180 seconds
    ├─ mvn verify
    ├─ mvn jacoco:report
    └─ Generate reports

180-300 seconds
    ├─ Publish test results
    ├─ Publish artifacts
    ├─ Publish coverage
    └─ Cleanup workspace

────────────────────────────
TOTAL: ~5-10 minutes per build
────────────────────────────

Typical breakdown:
├─ 30% Compilation & code quality checks
├─ 40% Test execution
├─ 20% Report generation
└─ 10% Publishing & cleanup
```

---

## Cost Analysis

```
Azure DevOps Pricing

Free Tier (Per Month)
├─ 1,800 minutes CI/CD
├─ 5 users max
├─ Public/private repos
├─ Built-in artifact storage
└─ Email notifications

Usage Example:
├─ 10 developers
├─ 10 commits per day per developer
├─ 5 minutes per build
└─ = 500 minutes/day × 20 days = 10,000 minutes/month
   └─ Free tier (1,800) NOT ENOUGH
   └─ Need paid tier: $100-200/month

Cost Saving vs Jenkins:
├─ Jenkins Server Cost: $500-2,000/month (hardware + maintenance)
├─ Azure DevOps Paid: $100-200/month
├─ Savings: $300-1,800/month
├─ ROI: < 1 month
└─ + No server maintenance (Microsoft handles it)
```

---

## Security & Access Control

```
Role-Based Access Control (RBAC)
┌─────────────────┐
│ Azure DevOps    │
│ Organization    │
└────────┬────────┘
         │
         ├─ Project Administrators
         │  ├─ Full control of pipelines
         │  ├─ Can delete pipelines
         │  └─ Can configure settings
         │
         ├─ Build Administrators
         │  ├─ Can edit pipelines
         │  ├─ Can queue builds
         │  └─ Can view logs
         │
         ├─ Contributors
         │  ├─ Can queue builds
         │  ├─ Can view results
         │  └─ Cannot edit pipeline
         │
         └─ Readers
            ├─ Can view results
            └─ Cannot queue builds

Authentication Methods
├─ Microsoft Account (SSO)
├─ Azure AD (Enterprise)
├─ GitHub Account
└─ Personal Access Tokens (API)

Artifact Security
├─ All artifacts encrypted in transit (TLS)
├─ All artifacts encrypted at rest
├─ Per-project access control
├─ Audit logging of all access
└─ Retention policies (auto-delete old builds)
```

---

## Troubleshooting Decision Tree

```
Pipeline Failed?
    │
    ├─ No logs visible
    │  └─ Wait for agent startup (1-2 min)
    │
    ├─ YAML syntax error
    │  ├─ Check indentation (2 spaces, no tabs)
    │  ├─ Validate at yamllint.com
    │  └─ Check trigger section
    │
    ├─ Maven not found
    │  └─ Ensure Maven@3 task present
    │
    ├─ Java version wrong
    │  ├─ Check variables section
    │  └─ Ensure JAVA_VERSION: '17'
    │
    ├─ Build fails (mvn compile)
    │  ├─ Check compilation errors in logs
    │  ├─ Verify dependencies in pom.xml
    │  └─ Run `mvn clean compile` locally first
    │
    ├─ Tests fail
    │  ├─ Expected if test data missing
    │  ├─ Check test output
    │  └─ Add test data to fixtures/
    │
    ├─ Code quality fails
    │  ├─ Set continueOnError: true (already set)
    │  ├─ Fix code issues locally
    │  └─ Re-push to trigger
    │
    ├─ Artifacts not published
    │  ├─ Check artifact paths exist
    │  ├─ continueOnError: true allows missing artifacts
    │  └─ Review file system output
    │
    └─ Unknown error
       ├─ Scroll through full logs
       ├─ Check for error messages
       ├─ See ADO_TROUBLESHOOTING.md
       └─ Contact Azure Support (free)
```

---

## Complete File Map

```
Repository Root
│
├─ azure-pipelines.yml
│  └─ Main pipeline configuration (THIS FILE)
│
├─ ADO_MIGRATION_GUIDE.md
│  └─ Comprehensive setup guide
│
├─ ADO_SETUP_STEPS.md
│  └─ Step-by-step walkthrough
│
├─ ADO_TROUBLESHOOTING.md
│  └─ Error resolution guide
│
├─ ADO_QUICK_REFERENCE.md
│  └─ Quick lookup cheat sheet
│
├─ ADO_MIGRATION_SUMMARY.md
│  └─ Executive summary & status
│
├─ pom.xml
│  └─ Maven build configuration
│
├─ src/
│  ├─ main/
│  │  ├─ java/
│  │  │  ├─ api/         (ApiClient, ApiUtils)
│  │  │  ├─ core/        (BaseTest, BrowserManager)
│  │  │  ├─ pages/       (LoginPage, HomePage)
│  │  │  └─ utils/       (Various utilities)
│  │  └─ resources/
│  │     ├─ config.properties
│  │     └─ logback.xml
│  │
│  └─ test/
│     ├─ java/
│     │  ├─ CucumberRunnerTest
│     │  ├─ cucumber/     (Hooks, context)
│     │  └─ steps/        (Step definitions)
│     └─ resources/
│        ├─ features/     (Feature files)
│        ├─ fixtures/     (Test data)
│        └─ HTML/JSON     (Test pages)
│
└─ target/
   ├─ classes/          (Compiled classes)
   ├─ surefire-reports/ (JUnit results)
   ├─ site/jacoco/      (Code coverage)
   ├─ cucumber-reports/ (Cucumber results)
   ├─ test-artifacts/   (Screenshots, videos)
   └─ a11y-reports/     (Accessibility results)
```

---

## Next Steps Sequence

1. **Create Azure DevOps Account** (5 min)
   - Go to dev.azure.com
   - Sign in with Microsoft Account
   - Create organization

2. **Create Project** (3 min)
   - Click "New project"
   - Name: demoPlaywright
   - Choose visibility

3. **Connect Repository** (5 min)
   - Get Azure Repos URL
   - Push code: `git push -u origin main develop hemendra`
   - Verify files in Azure DevOps > Repos

4. **Create Pipeline** (2 min)
   - Pipelines > New Pipeline
   - Select "Existing Azure Pipelines YAML file"
   - Choose azure-pipelines.yml
   - Click "Save and run"

5. **Monitor Execution** (10 min)
   - Wait for pipeline to complete
   - Check for errors in logs
   - Review test results

6. **Verify Artifacts** (5 min)
   - Check Artifacts tab
   - Download reports
   - Verify coverage metrics

7. **Test Automatic Trigger** (5 min)
   - Make code change
   - Push to main
   - Verify pipeline triggers

**Total Setup Time: ~35 minutes**

---

## Success Indicators

✅ Pipeline running successfully when you see:
- All stages completed (green checkmarks)
- Test results in Tests tab
- Code coverage > 0%
- Artifacts available for download
- No errors in logs
- Email notification received

❌ Common issues (all fixable):
- YAML syntax errors → Validate at yamllint.com
- Maven not found → Ensure Maven@3 task
- Tests failing → Expected if test data missing
- Artifacts missing → Check paths in logs

---

## Key Takeaways

1. **No Local Installation** - Azure DevOps is cloud-based
2. **Automatic Agents** - Microsoft manages servers
3. **Easy Setup** - ~30 minutes from zero to running
4. **Free Tier** - 1800 minutes/month per user
5. **All-in-One** - Code, pipelines, tests, coverage in one place
6. **No Maintenance** - Microsoft handles updates & security
7. **Better Visibility** - Integrated results & notifications
8. **Team Collaboration** - Easy to share & access

