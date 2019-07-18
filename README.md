# TestRail integrations
--------------------------

A Java client library [codepine](https://github.com/codepine/testrail-api-java-client).

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.ivanovvlad9626/TestRail/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.ivanovvlad9626/TestRail)

## Quick Start
--------------

### Maven Dependency
```xml
<dependency>
  <groupId>com.github.ivanovvlad9626</groupId>
  <artifactId>TestRail</artifactId>
  <version>LATEST</version>
</dependency>
```
### Gradle Dependency
```groovy
compile 'com.github.ivanovvlad9626:TestRail:+'
```

### Example Usage
* Create file **testRail.properties** in src/main/resources/
```properties
nameProject = Name project. This text add descriptions in testRung
hostTR = http://past-you-url
testRailIntegrations = true //Flag on/off integrations.
loginTR = Login in TestRail.
passTR = Password in TestRail.
newMilestone = Name Milestone "Test integrations".
openMilestone = If you have Milestone. Paste this name milestone.
testRun = If you have TestRun. Paste this name TestRun.
regress = true //If you need not close Milestone, if all test passed. false close Milestone if all test passed.
nameSection = Name Section in TestRail contains test cases.
project = Name Project in TestRail.
```
* Add listener in TestNG open Run/Debug Configuration, Templates -> TestNG -> Listeners -> press plus btn -> paste name class TestRailListener or use gradle
```
ru.integrations.testRail.listeners.TestRailListener
```
Example gradle
```groovy
test {
    useTestNG() {
        listeners << 'ru.integrations.testRail.listeners.TestRailListener'

    }
    testLogging {
        showStandardStreams = true
        events "PASSED", "FAILED", "SKIPPED"
        exceptionFormat = "full"
    }

    test.outputs.upToDateWhen { false }
}
```

* If you use Rest-Assured, then you can use the ListenerRestAssured listener. He will add to the request and response, in the result of the cases that did not pass. 

Add this code with method initialization RestAssured data. 
```java
 public static RequestSpecification base() {
        RequestSpecification builder = RestAssured.given();
        builder.baseUri("test.test.com");
        builder.basePath("/api");
        builder.port(5010);
        builder.filter(new ListenerRestAssured());
        return builder;
    }
    
    public void getProfile(){
        RestAssured
                .given()
                .spec(base())
                .get("/profile")
                .then()
                .statusCode(200);
    }
```
* Field annotation in test method.
```java
@TestRail(CaseName="Case name in TestRail")
@Test
public void test(){
    
}
```

## License
----------
This project is licensed under [Apache License, Version 2.0](https://opensource.org/licenses/Apache-2.0).