# Azure DevOps Pipeline - Quick Reference Card

## 📋 TL;DR - Azure DevOps Migration

**No Installation Needed.** Azure DevOps is cloud-based like GitHub Actions.

### 3-Step Setup
1. Create account at dev.azure.com
2. Push code with `azure-pipelines.yml` 
3. Create pipeline in Azure DevOps UI
4. Done! ✓ Runs automatically on every push

---

## 🚀 Current Pipeline Features

### Stages
```
Build & Test
├── Checkout
├── Build (compile only)
├── Code Quality (Checkstyle, PMD, SpotBugs)
├── Unit Tests
├── Generate Reports
└── Publish (JaCoCo, JUnit, Cucumber, Artifacts)

Reports & Summary
└── Build Summary

Cleanup
└── Remove artifacts
```

### What Runs
- **Automatic Triggers:** Push to `main`, `develop`, or `hemendra` branches
- **Pull Request Triggers:** On PR to `main` or `develop`
- **Agent:** ubuntu-latest (Linux)
- **Duration:** ~5-10 minutes per run
- **Cost:** Free tier (1800 minutes/month)

---

## 📁 File Reference

| File | Purpose |
|------|---------|
| `azure-pipelines.yml` | Pipeline configuration (NEW) |
| `Jenkinsfile` | Old Jenkins config (can delete) |
| `pom.xml` | Maven build config |
| `ADO_MIGRATION_GUIDE.md` | Setup instructions |
| `ADO_TROUBLESHOOTING.md` | Error fixes |

---

## 🔍 Key Pipeline Sections

### Triggers
```yaml
trigger:
  - main
  - develop
  - hemendra

pr:
  - main
  - develop
```
**What it does:** Runs pipeline on any push to these branches, or PR to main/develop.

### Variables
```yaml
variables:
  JAVA_VERSION: '17'
  MAVEN_VERSION: '3.9.0'
```
**What it does:** Auto-installs Java 17 and Maven 3.9.0 on build agent.

### Pool
```yaml
pool:
  vmImage: 'ubuntu-latest'
```
**Options:**
- `ubuntu-latest` (Linux) - **Current**
- `windows-latest` (Windows)
- `macos-latest` (macOS)

### Tasks
```yaml
- task: Maven@3          # Run Maven goals
- task: PublishTestResults@2  # Publish JUnit/Cucumber
- task: PublishCodeCoverageResults@1  # Publish JaCoCo
- task: PublishBuildArtifacts@1  # Publish screenshots/videos
- script: echo "Custom command"  # Run shell script
```

---

## 🛠️ Common Commands in Pipeline

### Maven Compile
```bash
mvn clean compile -DskipTests
```
**When:** Build stage

### Maven Test
```bash
mvn test
```
**When:** Unit test stage

### Maven Code Quality
```bash
mvn checkstyle:check
mvn pmd:check
mvn spotbugs:check
```
**When:** Code quality stage

### Maven Reports
```bash
mvn verify
mvn jacoco:report
```
**When:** Report generation stage

---

## 📊 Output & Artifacts

### Published Artifacts
```
Azure DevOps > Build > Artifacts
├── Cucumber-Report/
│   └── cucumber-report.html
├── Test-Screenshots/
│   └── (screenshots)
├── Test-Videos/
│   └── (videos on failure)
├── JaCoCo-Report/
│   └── (code coverage)
└── Accessibility-Reports/
    └── (a11y reports)
```

### Test Results
```
Azure DevOps > Pipelines > Tests
├── JUnit Test Results (from surefire-reports/*.xml)
├── Cucumber Results (from cucumber-report.json)
└── Code Coverage (JaCoCo report)
```

---

## ✅ Verification Checklist

- [ ] Azure DevOps account created
- [ ] Project created
- [ ] Repository connected (Azure Repos / GitHub / GitLab)
- [ ] `azure-pipelines.yml` pushed to repo
- [ ] Pipeline created in Azure DevOps
- [ ] First run triggered automatically
- [ ] All stages passed (or acceptable failures only)
- [ ] Artifacts visible in build results
- [ ] (Optional) Delete Jenkinsfile

---

## 🐛 Quick Troubleshooting

| Problem | Solution |
|---------|----------|
| Pipeline won't start | Check YAML syntax at yamllint.com |
| Maven not found | Ensure Maven@3 task is present |
| Tests failing | Check if test data exists; set continueOnError: true |
| Artifacts not publishing | Verify paths exist; add continueOnError: true |
| Code quality checks failing | Set continueOnError: true to not block pipeline |
| Java version issue | Agent has Java 17; verify in logs |

Full troubleshooting guide: See `ADO_TROUBLESHOOTING.md`

---

## 🔗 Useful Links

| Resource | URL |
|----------|-----|
| Azure DevOps | https://dev.azure.com |
| Pipeline Docs | https://docs.microsoft.com/azure/devops/pipelines |
| YAML Schema | https://docs.microsoft.com/azure/devops/pipelines/yaml-schema |
| Task Reference | https://docs.microsoft.com/azure/devops/pipelines/tasks |
| YAML Validator | https://www.yamllint.com |

---

## 📞 Common Questions

**Q: Will pipeline run on my machine?**
A: No. Runs on Microsoft's ubuntu-latest server in the cloud.

**Q: Do I need to install anything locally?**
A: No. Just create Azure DevOps account (free) and push code.

**Q: Can I run it locally for testing?**
A: Yes, but pipeline runs in cloud. To test locally:
```bash
mvn clean test
mvn verify
mvn jacoco:report
```

**Q: How much does it cost?**
A: Free tier includes 1800 free minutes per month. Enterprise has more.

**Q: Can I still use Jenkins?**
A: Yes, both can run. But goal is to migrate to ADO and decommission Jenkins.

**Q: What if pipeline fails?**
A: Check logs in Azure DevOps → Build Run → Logs. See `ADO_TROUBLESHOOTING.md` for common fixes.

**Q: How do I debug?**
A: Add debug logging in scripts:
```yaml
- script: |
    set -x  # Enable debug
    mvn clean compile
```

---

## 📋 Migration Checklist

### Before Migration
- [ ] Ensure local build passes: `mvn clean test`
- [ ] Verify all features and steps are correct
- [ ] Confirm test data is available
- [ ] Review pom.xml is correct

### During Migration
- [ ] Create `azure-pipelines.yml` (✓ Done)
- [ ] Create Azure DevOps account
- [ ] Create project in ADO
- [ ] Push code with YAML file
- [ ] Create pipeline in ADO UI
- [ ] Run first build

### After Migration
- [ ] Verify pipeline passes
- [ ] Check all artifacts publish
- [ ] Review test results
- [ ] Fix any issues (see troubleshooting)
- [ ] Delete Jenkinsfile (when ready)
- [ ] Decommission Jenkins (when ready)

---

## 🎯 Success Criteria

Pipeline is working when:
- ✓ Stages complete without errors
- ✓ Test results published
- ✓ Code coverage shown
- ✓ Artifacts visible
- ✓ Duration < 10 minutes
- ✓ No manual intervention needed

---

## 🔔 Branch Protection (Optional)

To require pipeline to pass before merging:

1. Go to **Repos** → **Branches**
2. Click **⋯** on main branch
3. Select **Branch policies**
4. Under **Build validation**, click **Add**
5. Select pipeline: `azure-pipelines`
6. Set to **Required**
7. Save

Now PRs can't merge unless pipeline passes. ✓

---

## 📊 Viewing Results

### Pipeline Runs
```
Azure DevOps > Pipelines > Pipelines
→ Click pipeline name
→ Click run number
→ View all stages & logs
```

### Test Results
```
Azure DevOps > Pipelines > Test Results
→ See all test results
→ Filter by passed/failed
→ View failure details
```

### Code Coverage
```
Azure DevOps > Pipelines > Pipeline Name > Run
→ Summary tab
→ View code coverage %
→ Click to see file details
```

### Artifacts
```
Azure DevOps > Pipelines > Pipeline Name > Run
→ Artifacts tab
→ Download reports/screenshots/videos
```

---

## 🚨 If Everything Fails

1. Check YAML with yamllint.com
2. Test Maven locally: `mvn clean test`
3. Review logs in Azure DevOps
4. Check agent availability
5. Simplify pipeline to single script step
6. Contact Azure Support (free tier)

For detailed troubleshooting, see: **ADO_TROUBLESHOOTING.md**

