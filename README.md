# API Test Automation

This project provides a robust framework for API test automation using **Cucumber** and **TestNG**. It supports parallel execution, dynamic JSON schema validation, and tag-based scenario filtering.

---

## Features
- **Environment Selection:** Easily run tests on different environments (e.g., staging, production).
- **Parallel and Single Execution:** Supports multi-threaded execution for faster test runs.
- **Cucumber Tag Filtering:** Run specific scenarios using custom tags.
- **Dynamic JSON Schema Validation:** Validate API responses against predefined JSON schemas.

---

## Commands

### 1. Environment Selection
Run tests in a specific environment:
```bash
mvn test -Denv=<environment>

mvn clean test -Dparallel.mode=false
#Parellel
mvn clean test -Dparallel.mode=true


--- TODO
# Parallel
mvn clean test -DsuiteXmlFile=src/test/resources/testng-parallel.xml

# Single Thread Test
mvn clean test -DsuiteXmlFile=src/test/resources/testng-single.xml