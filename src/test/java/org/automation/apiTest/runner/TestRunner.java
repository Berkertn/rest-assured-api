package org.automation.apiTest.runner;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

import static org.automation.apiTest.utils.LoggerUtil.logInfo;

@CucumberOptions(
        features = "features",
        glue = {"org.automation.apiTest.steps", "org.automation.apiTest.utils", "org.automation.apiTest.configurations"},
        plugin = {
                "pretty",
                "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"  // Extent Reports Plugin
        },
        tags = "@api and not @Ignore"
)
public class TestRunner extends AbstractTestNGCucumberTests {
    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        boolean isParallel = Boolean.parseBoolean(System.getProperty("parallel.mode", "false"));
        setParallelMode(isParallel);
        if (!isParallel) {
            logInfo("Overriding parallel execution to single-thread mode.");
            System.setProperty("dataproviderthreadcount", "1");
        } else {
            System.setProperty("dataproviderthreadcount", "4");
        }
        return super.scenarios();
    }

    private void setParallelMode(boolean isParallel) {
        if (isParallel) {
            logInfo("Running tests in parallel mode.");
        } else {
            logInfo("Running tests in single-thread mode.");
        }
    }
}
