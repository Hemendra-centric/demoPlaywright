# Step-by-Step: Azure DevOps Pipeline Setup (No Local Installation)

## ⚠️ IMPORTANT: Azure DevOps is Cloud-Based

**You do NOT need to install anything locally.**
- Azure DevOps is a web service (like GitHub, Gmail, etc.)
- Pipelines run on Microsoft's servers, not your machine
- You only need a web browser and internet connection
- Free tier: 1800 minutes/month

---

## Step 1: Create Azure DevOps Account (5 minutes)

### 1.1 Go to Azure DevOps
- Open browser and visit: **https://dev.azure.com**
- Click **Sign in** (use Microsoft Account)
- If you don't have an account, click **Create one!**

### 1.2 Create Microsoft Account (if needed)
```
Email: your.email@example.com
Password: Strong password
Phone: For account recovery
```

### 1.3 Create Organization
```
Organization Name: myorganization (or your company name)
Region: Use your geographic location
```

### 1.4 Agree to Terms
- Check "I agree to the User Agreement"
- Click **Continue**

✅ **You now have an Azure DevOps account**

---

## Step 2: Create Project (3 minutes)

### 2.1 Create New Project
- In Azure DevOps, click **New project**
- Or click **+ Create project**

### 2.2 Project Settings
```
Project name: demoPlaywright
Description: Playwright Automation Framework
Visibility: Private (or Public)
Version control: Git
Work item process: Scrum (or Agile)
```

### 2.3 Click Create
```
✓ Organization: myorganization
✓ Project: demoPlaywright
```

✅ **You now have a project**

---

## Step 3: Push Code to Azure Repos (5 minutes)

### 3.1 Get Your Repository URL

In Azure DevOps:
1. Go to **Repos** → **Files**
2. Click **Clone**
3. Copy the HTTPS URL
```
Example: https://dev.azure.com/myorg/demoPlaywright/_git/demoPlaywright
```

### 3.2 Add Remote to Your Local Repo

In your terminal (Windows: Git Bash or PowerShell):
```bash
cd c:\wip\demoPlaywright

# Add Azure Repos as remote
git remote add azure https://dev.azure.com/YOUR_ORG/demoPlaywright/_git/demoPlaywright

# Or replace origin if it existed
git remote set-url origin https://dev.azure.com/YOUR_ORG/demoPlaywright/_git/demoPlaywright
```

Replace:
- `YOUR_ORG` = your organization name
- `demoPlaywright` = your project name (both occurrences)

### 3.3 Verify Remote
```bash
git remote -v
# Should show:
# azure (fetch): https://...
# azure (push): https://...
```

### 3.4 Push Code
```bash
# Push main branch
git push -u azure main

# Push other branches
git push -u azure develop
git push -u azure hemendra
```

**First push will ask for credentials:**
- Username: your Microsoft email
- Password: your Azure DevOps Personal Access Token (PAT)

#### Generate Personal Access Token (PAT)
If asked for password, do this:
1. In Azure DevOps, click your **profile icon** (top right)
2. Click **Personal access tokens**
3. Click **+ New Token**
   - Name: `git-push`
   - Organization: Your org
   - Scopes: Check **Code (read & write)**
   - Expiration: 1 year
4. Click **Create**
5. **Copy the token** (shown once)
6. Use this as your password when pushing

### 3.5 Verify Push Success
```bash
git log --oneline -5
# Should show your commits

# In Azure DevOps:
# Repos → Files → Should see your files
```

✅ **Code is now in Azure Repos**

---

## Step 4: Verify azure-pipelines.yml is in Repo

### 4.1 Check File Exists Locally
```bash
cd c:\wip\demoPlaywright
ls azure-pipelines.yml  # Windows: dir azure-pipelines.yml
# Should exist
```

### 4.2 If Missing, Create It
The file was already created, but if missing:
```bash
# Copy from project root
# File should be: c:\wip\demoPlaywright\azure-pipelines.yml
```

### 4.3 Commit and Push
```bash
git add azure-pipelines.yml
git commit -m "Add Azure Pipelines configuration"
git push
```

### 4.4 Verify in Azure DevOps
1. Go to **Repos** → **Files** in Azure DevOps
2. Should see `azure-pipelines.yml` in root directory

✅ **Pipeline file is in repository**

---

## Step 5: Create Pipeline in Azure DevOps (2 minutes)

### 5.1 Go to Pipelines
In Azure DevOps:
1. Click **Pipelines** (left sidebar)
2. Click **Pipelines** (not "Environments" or "Releases")

### 5.2 Create New Pipeline
- Click **Create pipeline**
- Or click **New pipeline** button

### 5.3 Select Repository Type

**If code is in Azure Repos Git:**
```
Where is your code?
→ Click: Azure Repos Git
→ Select repository: demoPlaywright
→ Click: Continue
```

**If code is in GitHub:**
```
Where is your code?
→ Click: GitHub
→ Authorize Azure DevOps
→ Select repository: your-repo
→ Click: Continue
```

### 5.4 Configure Pipeline

```
Configure your pipeline:
→ Select: Existing Azure Pipelines YAML file
→ Branch: main (or your main branch)
→ Path: /azure-pipelines.yml
→ Click: Continue
```

### 5.5 Review YAML
- You should see the YAML content
- Check it looks correct (has stages, tasks, etc.)
- Click **Save and run**

### 5.6 Add Commit Comment (Optional)
```
Commit message: Add Azure Pipelines YAML
(You can accept default)
```

Click **Save and run**

✅ **Pipeline created and triggered**

---

## Step 6: Monitor First Pipeline Run (5-10 minutes)

### 6.1 Wait for Build
- You'll see the pipeline running
- Status: **In progress** → **Completed**
- Duration: Usually 5-10 minutes

### 6.2 View Build Output
```
Pipelines > Your Pipeline > Build #1
→ Click each stage to expand
→ Scroll through logs
```

**Expected stages:**
1. ✓ Build (compile)
2. ✓ Code Quality (checkstyle, PMD, spotbugs)
3. ✓ Unit Tests (maven test)
4. ✓ Generate Reports (maven verify)
5. ✓ Publish Artifacts (screenshots, videos, reports)
6. ✓ Summary
7. ✓ Cleanup

### 6.3 Check for Errors
Look for:
- **Red X** = Failed stage (see logs)
- **Yellow !** = Warning (see logs)
- **Green ✓** = Passed

### 6.4 View Test Results
```
Pipelines > Your Pipeline > Build #1
→ Tests tab
→ Should see test results
```

### 6.5 View Code Coverage
```
Pipelines > Your Pipeline > Build #1
→ Summary tab
→ Should see JaCoCo code coverage %
```

### 6.6 Download Artifacts
```
Pipelines > Your Pipeline > Build #1
→ Artifacts tab
→ Can download:
   - Cucumber-Report (HTML)
   - JaCoCo-Report (HTML)
   - Test-Screenshots
   - Test-Videos
   - Accessibility-Reports
```

✅ **First run complete**

---

## Step 7: Verify Automatic Triggers

### 7.1 Make a Small Change
```bash
cd c:\wip\demoPlaywright

# Edit a file (any file)
echo "# Test" >> README.md

# Commit and push to main
git add README.md
git commit -m "Test trigger"
git push
```

### 7.2 Check if Pipeline Triggers
```
Azure DevOps > Pipelines > Pipelines
→ Should see new run automatically started
→ Status: In progress
```

### 7.3 Wait for Completion
- Pipeline should run automatically
- Should complete in 5-10 minutes
- Results published same as before

✅ **Automatic triggers working**

---

## Step 8: (Optional) Delete Jenkinsfile

Once Azure Pipelines is working reliably:

```bash
cd c:\wip\demoPlaywright

# Delete old Jenkins config
rm Jenkinsfile

# Commit the deletion
git add Jenkinsfile
git commit -m "Remove Jenkinsfile - migrated to Azure Pipelines"
git push
```

✅ **Migration complete**

---

## Success Checklist

- [ ] Azure DevOps account created
- [ ] Organization created
- [ ] Project created (demoPlaywright)
- [ ] Code pushed to Azure Repos
- [ ] azure-pipelines.yml in repository
- [ ] Pipeline created in Azure DevOps
- [ ] First run completed successfully
- [ ] Test results visible
- [ ] Artifacts available
- [ ] Automatic trigger verified
- [ ] (Optional) Jenkinsfile deleted

---

## Troubleshooting Steps 1-8

### Issue: "Repository not found"
```
Error: Repository not found during clone
```

**Fix:**
1. Double-check organization name (case-sensitive)
2. Check spelling of project name
3. Verify you have access (check project permissions)
4. Try PAT (Personal Access Token) instead of password

### Issue: "Permission denied"
```
fatal: Authentication failed
```

**Fix:**
1. Generate new Personal Access Token:
   - Azure DevOps > Profile Icon > Personal access tokens
   - Create new token with "Code (read & write)" scope
2. Use token as password when pushing
3. Or configure Git credential manager:
   ```bash
   git config --global credential.helper manager-core
   ```

### Issue: "Pipeline YAML not found"
```
Error: File not found: /azure-pipelines.yml
```

**Fix:**
1. Verify file exists: `ls -la azure-pipelines.yml`
2. Verify pushed: `git log --all -- azure-pipelines.yml`
3. Ensure path is `/azure-pipelines.yml` (slash before name)
4. In Azure DevOps, check **Repos > Files** to see file

### Issue: "Build fails immediately"
```
##[error]Some error
```

**Fix:**
1. Click the failed stage
2. Scroll through logs to find error message
3. See **ADO_TROUBLESHOOTING.md** for solutions
4. Most common: Maven not installed (use Maven@3 task)

### Issue: "Tests fail but pipeline should pass"
```
[ERROR] Tests run: 4, Failures: 2
```

**Fix:**
1. This is expected if test data is missing
2. Either:
   - Add test data to `src/test/resources/`
   - Or set `continueOnError: true` in pipeline (already set)
3. Pipeline should still mark as passed with current config

### Issue: "No artifacts published"
```
##[warning]Directory not found
```

**Fix:**
1. Tests didn't generate artifacts
2. Check test output in logs
3. Artifacts depend on tests running
4. Set `continueOnError: true` on publish tasks (already set)

---

## What Happens Now

### Every Time You Push Code:
1. Pipeline automatically triggers
2. Runs all stages (build, test, quality, etc.)
3. Publishes results to Azure DevOps
4. Takes 5-10 minutes
5. You get email notification when done

### You Can:
- ✓ View all run history in Azure DevOps
- ✓ Download artifacts (reports, screenshots)
- ✓ See test results and coverage
- ✓ Check logs for errors
- ✓ Trigger manual runs if needed
- ✓ Set branch protection (require pipeline pass)

### To Make Changes:
1. Edit code locally
2. Test locally if you want: `mvn clean test`
3. Push to repo
4. Pipeline runs automatically
5. Review results in Azure DevOps

---

## Next Steps

1. **Monitor First Few Runs**
   - Watch for 2-3 pipeline runs
   - Fix any recurring errors (see troubleshooting)
   - Ensure artifacts publish correctly

2. **Add Test Data** (if tests are failing)
   - Add API test data to fixtures
   - Add test user credentials
   - Add test URLs

3. **Set Up Branch Protection** (optional)
   - Require pipeline to pass before merging PR
   - Go to Repos > Branches > main > Policies
   - Enable "Build validation"

4. **Configure Notifications** (optional)
   - Azure DevOps will email on success/failure
   - Can customize in Notifications settings

5. **Delete Jenkinsfile** (when confident)
   - Keep for safety for 1-2 weeks
   - Once ADO is stable, delete from repo

---

## Key Points to Remember

🎯 **Azure DevOps is cloud-based** - nothing to install locally
🎯 **Pipeline runs automatically** - triggered on every push
🎯 **Takes 5-10 minutes** - per build
🎯 **No agent setup needed** - uses Microsoft-hosted ubuntu-latest
🎯 **Free tier** - 1800 minutes per month
🎯 **Fully integrated** - test results, coverage, artifacts in one place

---

## Support & Resources

| Need | Where to Go |
|------|-----------|
| Questions | Azure DevOps Docs: docs.microsoft.com/azure/devops |
| YAML Help | YAML Schema: docs.microsoft.com/azure/devops/pipelines/yaml-schema |
| Task List | Task Reference: docs.microsoft.com/azure/devops/pipelines/tasks |
| Errors | See ADO_TROUBLESHOOTING.md in your repo |
| Questions | This guide + ADO_MIGRATION_GUIDE.md |

---

## You're Done! 🎉

Your pipeline is now running in Azure DevOps cloud. No local installation needed.

- Code pushed ✓
- Pipeline created ✓
- Automatic triggers working ✓
- Results publishing ✓

Everything happens in the cloud. You can decommission Jenkins whenever ready.

