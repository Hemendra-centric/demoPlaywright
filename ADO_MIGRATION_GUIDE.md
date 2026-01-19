# Azure DevOps Pipeline Migration Guide

## Quick Answer: Do You Need to Install Azure DevOps Locally?

**NO.** Azure DevOps is **100% cloud-based**. You don't install anything on your machine.

---

## What is Azure DevOps?

| Aspect | Details |
|--------|---------|
| **Platform** | Cloud-based CI/CD service (like GitHub Actions) |
| **Access** | Web browser at `dev.azure.com` |
| **Build Agents** | Run on Microsoft-hosted servers (ubuntu-latest, windows-latest, etc.) |
| **Local Installation** | NOT required |
| **Configuration** | YAML file (`azure-pipelines.yml`) in your repository |

---

## Setup Steps (No Local Installation Needed)

### Step 1: Create Azure DevOps Account
1. Go to https://dev.azure.com
2. Sign in with Microsoft Account (free tier available)
3. Create a new organization or use existing one

### Step 2: Create a Project
1. In Azure DevOps, click "Create project"
2. Enter project name (e.g., `demoPlaywright`)
3. Choose visibility (Private/Public)
4. Click "Create"

### Step 3: Push Code to Azure Repos
```bash
# Option A: If you don't have a remote yet
git remote add origin https://dev.azure.com/YOUR_ORG/YOUR_PROJECT/_git/YOUR_REPO
git push -u origin main
git push origin develop
git push origin hemendra

# Option B: If you have GitHub/GitLab repo
# Connect it to Azure DevOps as external source
```

### Step 4: Create the Pipeline
1. In Azure DevOps project, go to **Pipelines** → **Pipelines**
2. Click **New pipeline**
3. Select **Azure Repos Git**
4. Select your repository
5. Click **Existing Azure Pipelines YAML file**
6. Select **azure-pipelines.yml** from root
7. Click **Continue**
8. Click **Save and run**

### Step 5: Pipeline Runs Automatically
- Anytime you push to `main`, `develop`, or `hemendra` branches → pipeline triggers
- Pipeline runs on Microsoft's servers (not your machine)
- View results in Azure DevOps web UI
- Artifacts automatically published

---

## Key Differences: Jenkins vs Azure DevOps

| Feature | Jenkins | Azure DevOps |
|---------|---------|--------------|
| **Installation** | On-premise on your server | Cloud-based, nothing to install |
| **Execution** | Your Jenkins server | Microsoft-hosted agents |
| **Configuration** | Jenkinsfile | azure-pipelines.yml |
| **UI** | Jenkins web interface | Azure DevOps portal |
| **Triggers** | Git hooks, manual | Automatic on commit |
| **Artifacts** | Manual artifact server | Built-in artifact storage |
| **Cost** | Server + maintenance | Free tier (generous limits) |

---

## Pipeline Stages Mapped

### Old Jenkins → New Azure DevOps
```
Checkout              → Automatic (Checkout@1 task)
Build                 → Maven clean compile (Script task)
Code Quality          → Checkstyle, PMD, SpotBugs (3 Script tasks)
Unit Tests            → Maven test (Script task)
Generate Reports      → Maven verify (Script task)
Code Coverage         → JaCoCo report (Script + PublishCodeCoverageResults)
Publish Test Results  → PublishTestResults@2 task
Post-build Cleanup    → Cleanup stage (always runs)
```

---

## Testing the Pipeline

### Option 1: Test Locally Before Pushing (Recommended)
```bash
# Ensure build passes locally first
mvn clean test

# Check if azure-pipelines.yml is valid
# (Azure DevOps validates on commit)
```

### Option 2: Test in Azure DevOps
1. Push `azure-pipelines.yml` to your repo
2. Go to **Pipelines** in Azure DevOps
3. Click **New pipeline**
4. Select the YAML file
5. Click **Save and run** (will show any validation errors)

### Option 3: Use Azure Pipelines Validator
- Online tool: https://dev.azure.com/YOUR_ORG/YOUR_PROJECT/_build
- It validates YAML syntax before running

---

## Common Issues & Solutions

### Issue 1: Pipeline Can't Find Maven
**Solution:** Pipeline uses `Maven@3` task which auto-installs. No manual setup needed.

### Issue 2: Tests Failing
**Solution:** Same tests as before. Check if:
- Test data is available
- API endpoints are accessible
- Fixtures are in place

### Issue 3: Artifacts Not Published
**Solution:** Check artifact paths in `PublishBuildArtifacts@1` tasks:
```yaml
- task: PublishBuildArtifacts@1
  inputs:
    PathtoPublish: '$(System.DefaultWorkingDirectory)/target/cucumber-reports'
```

### Issue 4: Code Coverage Not Publishing
**Solution:** JaCoCo must generate the report first:
```bash
mvn jacoco:report
```
The pipeline already does this in `PublishCodeCoverageResults@1` task.

---

## Environmental Differences

### Jenkins Worked With:
```
MAVEN_HOME = '/usr/share/maven'
JAVA_HOME = '/usr/lib/jvm/java-17-openjdk'
```

### Azure DevOps Uses:
```
vmImage: 'ubuntu-latest'  # Equivalent to Jenkins Linux agent
JAVA_VERSION: '17'        # Auto-installed
MAVEN_VERSION: '3.9.0'    # Auto-installed
```

**No configuration needed** — Azure handles Java and Maven setup automatically.

---

## File Structure After Migration

```
your-repo/
├── azure-pipelines.yml    ← NEW (Azure DevOps config)
├── Jenkinsfile            ← OLD (can delete once ADO works)
├── pom.xml
├── src/
├── target/
└── README.md
```

**You can safely delete `Jenkinsfile` once the Azure pipeline works.**

---

## Verification Checklist

- [ ] Azure DevOps account created
- [ ] Project created in Azure DevOps
- [ ] Code pushed to Azure Repos (or GitHub/GitLab connected)
- [ ] `azure-pipelines.yml` committed to repo
- [ ] Pipeline created in Azure DevOps
- [ ] Pipeline triggered on first run
- [ ] All stages completed successfully
- [ ] Artifacts published and visible
- [ ] No errors in pipeline logs
- [ ] (Optional) Delete `Jenkinsfile` from repo

---

## Next Steps

1. **Create Azure DevOps account** at dev.azure.com (free)
2. **Create project** named `demoPlaywright`
3. **Push code** including `azure-pipelines.yml`
4. **Create pipeline** by selecting the YAML file
5. **Monitor execution** in Azure DevOps UI
6. **Fix any errors** that come up
7. **Delete `Jenkinsfile`** once everything works

---

## Support Resources

- **Azure Pipelines Docs:** https://docs.microsoft.com/azure/devops/pipelines
- **YAML Schema:** https://docs.microsoft.com/azure/devops/pipelines/yaml-schema
- **Task Reference:** https://docs.microsoft.com/azure/devops/pipelines/tasks
- **Marketplace Tasks:** https://marketplace.visualstudio.com/azuredevops

---

## Questions?

**Q: Will pipeline run on my machine?**  
A: No. Runs on Microsoft-hosted ubuntu-latest server.

**Q: Do I need Docker?**  
A: No. Ubuntu agent has Java 17 and Maven pre-installed.

**Q: Can I use different build agents?**  
A: Yes. Change `vmImage: 'ubuntu-latest'` to `windows-latest`, `macos-latest`, etc.

**Q: How much does it cost?**  
A: Free tier: 1800 minutes/month. Enterprise teams get more.

**Q: Can I keep using Jenkins?**  
A: Yes, but the goal is to migrate to ADO. Once working, you can decommission Jenkins.

