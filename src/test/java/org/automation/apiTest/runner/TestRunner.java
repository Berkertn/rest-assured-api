package org.automation.apiTest.runner;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

@CucumberOptions(
        features = "features",
        glue = "org.automation.apiTest.stepdefinitions",
        plugin = {"pretty", "json:target/cucumber-report.json"},
        tags = "@api"
)
public class TestRunner extends AbstractTestNGCucumberTests {
    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
