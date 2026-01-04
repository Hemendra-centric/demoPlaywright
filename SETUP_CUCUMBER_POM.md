# Required pom.xml Changes for Cucumber Support

Add the following dependencies to the `<dependencies>` section in `pom.xml` (before the closing `</dependencies>` tag):

```xml
<!-- Cucumber for BDD -->
<dependency>
    <groupId>io.cucumber</groupId>
    <artifactId>cucumber-java</artifactId>
    <version>7.14.0</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>io.cucumber</groupId>
    <artifactId>cucumber-junit</artifactId>
    <version>7.14.0</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>io.cucumber</groupId>
    <artifactId>cucumber-picocontainer</artifactId>
    <version>7.14.0</version>
    <scope>test</scope>
</dependency>
```

**File location in pom.xml**: Look for the line with `<artifactId>extentreports</artifactId>` (around line 95) and add the Cucumber dependencies right after it, before `</dependencies>`.

**Or use this command to patch pom.xml** (replace the entire file or manually add the dependencies):

```bash
# After adding dependencies, compile with:
mvn clean compile
```

Once pom.xml is updated with Cucumber dependencies, the new POM+Cucumber framework will be ready to use.
