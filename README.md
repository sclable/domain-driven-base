# domain-driven-base

## Usage
Head to [github](https://github.com/settings/tokens) and create a 
personal access token. Use that in your `~/.m2/settings.xml` (if the
file doesn't exists you have to create it):
```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                      http://maven.apache.org/xsd/settings-1.0.0.xsd">

  <activeProfiles>
    <activeProfile>github</activeProfile>
  </activeProfiles>

  <profiles>
    <profile>
      <id>github</id>
      <repositories>
        <repository>
          <id>github</id>
          <url>https://maven.pkg.github.com/sclable/domain-driven-base</url>
          <snapshots>
            <enabled>true</enabled>
          </snapshots>
        </repository>
      </repositories>
    </profile>
  </profiles>

  <servers>
    <server>
      <id>github</id>
      <username>!! YOUR GITHUB USERNAME HERE </username>
      <password>!! YOUR ACCESS TOKEN HERE </password>
    </server>
  </servers>
</settings>
```

After that you can import the library in your maven project (`pom.xml`)
like this:
```xml
<dependencies>
  <dependency>
    <groupId>com.sclable</groupId>
    <artifactId>domain-driven-base</artifactId>
    <version>1.0.0-SNAPSHOT</version>
  </dependency>
</dependencies>
```
