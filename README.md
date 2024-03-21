# Software Testing Course Projects

## Introduction

The course projects focus on writing tests for Baloot, an e-commerce system written using Spring Boot with a RESTful API.  
Each computer assignment (CA) includes writing different tests for Baloot, and also some explanatory questions about software testing topics which are answered in the corresponding report file.

## Assignments

### CA1 (JUnit)

Unit tests were written for the `Commodity`, `Comment`, and `User` models.  
The 3 unit test files can be found in `Baloot1/src/test/java/model`.  
Some tests make use of parameterized testing.  
The explanatory questions are about private method testing, multi-threaded testing, and finding a sample test code's problems.

### CA2 (Mock Testing)

Tests were written for the `CommoditiesController`, `CommentController`, and `AuthenticationController` classes.  
The 3 controller test files can be found in `Baloot1/src/test/java/controllers`.  
The tests mock the Baloot service class using the **Mockito** framework.  
The explanatory questions are about dependency injection, test double types, and classical and mockist testing strategies.

### CA3 (Graph Coverage)

Tests were written for the `Engine` and `Order` classes.  
The test files can be found in `Baloot2/src/test/java/domain`.  
The tests try to maximize branch and statement coverage which is calculated using the **JaCoCo** library.  
The explanatory questions are about the possibility of 100% branch or statement coverage, drawing the control flow graph of a code, and graph coverage prime and DU paths.

### CA4 (API Testing)

Tests were written for the `CommoditiesController` and `AuthenticationController` classes.  
The 2 test files which end in `ApiTest` can be found in `Baloot1/src/test/java/controllers`.  
The tests use the `@SpringBootTest` annotation in conjunction with **MockMvc** which performs API calls to an instance of the application and validates the JSON response.  
The explanatory questions are about logic coverage and input space partitioning.

### CA5 (Mutation Testing)

Using the tests from CA3 (*Baloot2*), mutation coverage was calculated using the **PITest** library.  
Mutation testing is used to check the quality of tests and how much they can detect faults.  
The coverage results are analyzed in the report in which we see that one mutant cannot be killed.  
A GitHub Actions workflow is also created (`.github/workflows/maven.yml`). The pipeline builds and runs the tests of Baloot projects after every push.

### CA6 (Behavior Driven Development)

Behavior-driven tests are written for `removeItemFromBuyList`, `withdrawCredit`, and `addCredit` methods of the `User` model.  
The test scenarios are in `Baloot1/src/test/resources` and the implementation can be found in `Baloot1/src/test/java/model/UserScenarioTest.java`.  
The scenarios are written using the **Cucumber** tool's Gherkin language.  
Recorded GUI testing is also performed on **Swagger UI**'s visualization of Baloot's API using **Katalon Recorder**.

---

[**PashaBarahimi**](https://github.com/PashaBarahimi) & [**MisaghM**](https://github.com/MisaghM)
