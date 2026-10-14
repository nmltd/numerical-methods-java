# New NMDEV project

This sample uses the published obfuscated combined `dev.nm:nmdev:1.2.6-SNAPSHOT` JAR from GitHub Packages. Java 8 or later and Maven are required.

## Authentication

Your GitHub account must have access to the private NMDEV package/repository. Create a personal access token (classic) with `read:packages` using [GitHub's instructions](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/managing-your-personal-access-tokens#creating-a-personal-access-token-classic).

Add the following server to `~/.m2/settings.xml` (merge it into existing settings):
```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.2.0">
  <servers>
    <server>
      <id>github-nmdev</id>
      <username>${env.GITHUB_USERNAME}</username>
      <password>${env.GITHUB_PACKAGES_TOKEN}</password>
    </server>
  </servers>
</settings>
```
Set those environment variables locally. Never commit a token.

## License, build and run

A trial license is included at `samples/new-nmdev-project/nm.lic`. The runtime loads this external file from the current working directory; run the commands below from this sample directory. Trial validity and entitlements are enforced by NMDEV. Replace it with your own valid license when the trial expires. The license is not embedded in the application JAR. Run these commands from this sample directory:
```bash
mvn -U clean verify
java -jar target/new-nm-project-1.0.0-SNAPSHOT.jar
java -cp "target/new-nm-project-1.0.0-SNAPSHOT.jar:target/lib/*" dev.nm.Graph
```
On Windows, replace the classpath colon with a semicolon. Alternatively:
```bash
mvn exec:java
mvn exec:java -Dexec.mainClass=dev.nm.Graph
```

HelloWorld prints `A number of columns = 2`. Graph prints shortest-path distances `7.0, 9.0, 11.0, 20.0, 20.0`. The existing JUnit test checks the matrix column count.

Packaging produces a runnable sample JAR and copies the single combined NMDEV runtime JAR into `target/lib/`. Keep both when distributing the sample. The library is already combined; another assembly/shading step is unnecessary.

Public [NMDEV Javadoc](https://nmltd.github.io/nmdev/).
