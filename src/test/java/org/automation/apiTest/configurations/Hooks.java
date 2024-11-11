package org.automation.apiTest.configurations;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.Scenario;
import org.automation.apiTest.context.ApiContext;

import java.io.File;
import java.io.IOException;

import static org.automation.apiTest.context.ApiContext.*;
import static org.automation.apiTest.utils.LoggerUtil.logInfo;

public class Hooks {

    private static ExtentReports extent;
    private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();

    @BeforeAll
    public void setupSuite() {
        String environment = System.getProperty("env", "dev");
        setEnv(environment);
        logInfo(String.format("Test are run on the [%s] environment", environment));
    }

    @Before(order = 0)
    public void setUp() throws IOException {
        logInfo("Test setup");
        if (extent == null) {
            ExtentSparkReporter sparkReporter = new ExtentSparkReporter("target/ExtentReport.html");
            sparkReporter.loadXMLConfig(new File("src/test/resources/extent-config.xml"));
            extent = new ExtentReports();
            extent.attachReporter(sparkReporter);
        }
    }

    @Before(order = 1)
    public void beforeScenario(Scenario scenario) {
        ExtentTest extentTest = extent.createTest(scenario.getName());
        test.set(extentTest);
    }

    @After
    public void afterScenario(Scenario scenario) {
        if (scenario.isFailed()) {
            logInfo(String.format("Request body: [%s]\n", getRequestBody()));
            logInfo(String.format("Response: [%s]", getResponse().getBody().prettyPrint()));
            logInfo("Scenario failed: " + scenario.getName());
            test.get().fail("Scenario failed: " + scenario.getName());
        } else {
            logInfo("Scenario failed: " + scenario.getName());
            test.get().pass("Scenario passed: " + scenario.getName());
        }
        extent.flush();
        ApiContext.clearTLVariables();
    }

    public static ExtentTest getTest() {
        return test.get();
    }
}
