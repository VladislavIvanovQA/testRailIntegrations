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
  <version>${stable.version.shown.above}</version>
</dependency>
```
### Gradle Dependency
```groovy
compile 'com.github.ivanovvlad9626:TestRail:0.2'
```

### Example Usage
* Create file **testRail.properties** 
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
* Add listener 
```
ru.integrations.testRail.listeners.TestRailListener
```
* If you use Rest-Assured, then you can use the ListenerRestAssured listener. He will add to the request and response, in the result of the cases that did not pass. 
```text
ru.integrations.testRail.listeners.ListenerRestAssured
```
* Field annotation in test method.
```text
@TestRail(CaseName="Case name in TestRail")
```

## License
----------
This project is licensed under [Apache License, Version 2.0](https://opensource.org/licenses/Apache-2.0).