# Integrating JUnit 5 with Spring Boot 1.5.x (Spring 4.3.x)

### HowTo Integrate JUnit5 with Spring 4.x and Spring Boot 1.5.x

Requires Java 8 or later (Junit 5 requirement)

See also:
* https://github.com/sbrannen/spring-test-junit5
* https://github.com/mockito/mockito/issues/445
* https://github.com/junit-team/junit5-samples/tree/master/junit5-mockito-extension
* http://mvpjava.com/spring-boot-junit5/
* https://stackoverflow.com/questions/21317006/spring-boot-parent-pom-when-you-already-have-a-parent-pom
* https://docs.spring.io/spring-boot/docs/current/reference/html/using-boot-build-systems.html#using-boot-maven-without-a-parent

##### Summary of what's needed to integrate JUnit5 with Spring 4.3/Spring Boot 1.5

######*Changes to the POM*

```xml
<parent>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-parent</artifactId>
  <version>1.5.4.RELEASE</version>
</parent>

<properties>
  <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
  <java.version>1.8</java.version>
  <junit-jupiter.version>5.0.1</junit-jupiter.version>
  <junit-platform.version>1.0.1</junit-platform.version>
  <mockito.version>2.10.0</mockito.version>
</properties>
...
...
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-test</artifactId>
  <scope>test</scope>
  <exclusions>
    <exclusion>
      <!-- exclude JUnit 4 -->
      <groupId>junit</groupId>
      <artifactId>junit</artifactId>
    </exclusion>
  </exclusions>
</dependency>
<!-- plus other spring modules you need -->
...
<!-- 
    JUnit 5 dependencies
    Spring 5 uses these version property names in parent POM
    so probably a good idea to stick with these
--> 
<dependency>
  <groupId>org.junit.jupiter</groupId>
  <artifactId>junit-jupiter-api</artifactId>
  <version>${junit-jupiter.version}</version>
  <scope>test</scope>
</dependency>
<dependency>
  <groupId>org.junit.jupiter</groupId>
  <artifactId>junit-jupiter-engine</artifactId>
  <version>${junit-jupiter.version}</version>
  <scope>test</scope>
</dependency>
<dependency>
  <groupId>org.junit.platform</groupId>
  <artifactId>junit-platform-runner</artifactId>
  <version>${junit-platform.version}</version>
  <scope>test</scope>
</dependency>
<dependency>
  <groupId>org.junit.platform</groupId>
  <artifactId>junit-platform-launcher</artifactId>
  <version>${junit-platform.version}</version>
</dependency>
...
<dependency>
  <groupId>org.mockito</groupId>
  <artifactId>mockito-core</artifactId>
  <version>${mockito.version}</version>
  <scope>test</scope>
</dependency>
<dependency>
  <!--
    needs SpringExtension, which is part of Spring 5/Boot 2, but not Spring 4.3.x/Boot 1.5.x
    see https://github.com/sbrannen/spring-test-junit5
    
    Currently, all that's needed to use the Spring TestContext Framework with JUnit 5 is to
    annotate a JUnit Jupiter based test class with @ExtendWith(SpringExtension.class)
    and whatever Spring annotations you need (e.g., @ContextConfiguration, @Transactional, 
    @Sql, etc.), but make sure you use @Test, @BeforeEach, etc. from the appropriate 
    org.junit.jupiter.api package.
  -->
  <groupId>com.github.sbrannen</groupId>
  <artifactId>spring-test-junit5</artifactId>
  <version>1.0.0</version>
  <scope>test</scope>
</dependency>
...
...
<!-- artifact spring-test-junit5 is published here: -->
<repositories>
  <repository>
    <!-- see https://github.com/sbrannen/spring-test-junit5 -->
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>
```

######*Add the MockitoExtension class*

A simple JUnit Jupiter extension to integrate Mockito into JUnit Jupiter to make tests somewhat simpler.

The `MockitoExtension` showcases the `TestInstancePostProcessor` and `ParameterResolver` extension APIs of 
JUnit Jupiter by providing dependency injection support at the field level and at the method parameter 
level via *Mockito* 2.x's `@Mock` annotation. This allows tests like:

```
@Test
@DisplayName("Checking @Mock Parameter is resolved")
void ensureDefaultRoleSetForNewEmployee(@Mock Role role) {
    assertNotNull(role);
    Employee employee = new Employee("anyone", role);
    assertEquals("anyone", employee.getName());
}
```

######*What to do if you can't use the Spring Boot Parent POM*

The example POM uses the standard Spring Boot Parent POM, i.e.
```xml
<parent>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-parent</artifactId>
  <version>1.5.4.RELEASE</version>
</parent>
```

If you can't use this because you already have a parent POM there are two choices:
  1. Add the Spring Boot Parent to the existing root parent. This is preferable since
  you still get the benefit of Spring maven plugin management and property overrides.
  2. Use a scope=import in the POM dependencyManagement element:
  ```xml
  <dependencyManagement>
    <dependencies>
      <dependency>
        <!-- Import dependency management from Spring Boot -->
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-dependencies</artifactId>
        <version>1.5.4.RELEASE</version>
        <type>pom</type>
        <scope>import</scope>
      </dependency>
    </dependencies>
  </dependencyManagement>
  ```
  You will also need to manually configure the compiler plugin for Java 8 (i.e. assuming the 
  property for ```java.version``` is set to 1.8):
  ```xml
  <plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
      <source>${java.version}</source>
      <target>${java.version}</target>
    </configuration>
  </plugin>
 ``` 
 Refer to the 
 [Spring Boot docs](https://docs.spring.io/spring-boot/docs/current/reference/html/using-boot-build-systems.html#using-boot-maven-without-a-parent) 
 for other gotchas using this approach
 
 
###### IDEs with and without JUnit5 support

For IDEs with JUnit5 support, make the following changes. This has the advantage that it prevents
accidental imports of JUnit3/4 into tests. This is only useful for projects that don't need to support 
legacy JUnit tests (i.e. you are starting from sratch, or have converted all legacy tests to JUnit5 tests).

1. Change the junit-platform-runner dependency to exclude the leagcy JUnit artifact:
```xml
    <dependency>
      <groupId>org.junit.platform</groupId>
      <artifactId>junit-platform-runner</artifactId>
      <version>${junit-platform.version}</version>
      <scope>test</scope>
      <exclusions>
        <exclusion>
          <!--
          exclude JUnit 4 if IDE has built-in support for JUnit 5
          otherwise, need to keep it included for support of @RunWith(JUnitPlatform.class)
          which runs JUnit5 tests in a JUnit 4 runner
          -->
          <groupId>junit</groupId>
          <artifactId>junit</artifactId>
        </exclusion>
      </exclusions>
    </dependency>
```
2. Under build/plugins in the POM, configure the maven-surefire-plugin to use the JUnit5 runner
```xml
  <plugin>
    <!--
    see https://www.petrikainulainen.net/programming/testing/junit-5-tutorial-running-unit-tests-with-maven/
    This configures maven for native JUnit 5 run support to avoid
    including the JUnit4 dependency, see the exludes above
    -->
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>2.19.1</version>
    <dependencies>
      <dependency>
        <groupId>org.junit.platform</groupId>
        <artifactId>junit-platform-surefire-provider</artifactId>
        <version>1.0.0</version>
      </dependency>
      <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter-engine</artifactId>
        <version>5.0.1</version>
      </dependency>
    </dependencies>
  </plugin>

```
 