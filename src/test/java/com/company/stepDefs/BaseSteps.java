package com.company.stepDefs;

import static utils.LogUtil.info;

import java.io.IOException;

import org.apache.commons.lang3.time.StopWatch;
import org.json.JSONException;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.testng.annotations.DataProvider;

import cucumber.api.CucumberOptions;
import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.testng.TestNGCucumberRunner;
import utils.BaseUtil;
import utils.SauceUtils;
import utils.WebDriverFactory;
import utils.listener.CustomWebDriverLogger;
import utils.properties.FrameworkProperties;

@CucumberOptions(features="src/test/resources/features", glue={"stepDefs"}, plugin= {"pretty", "html:target/cucumber-reports", "json:target/Cucumber.json", "rerun:target/re-run.txt"}, monochrome = true)
public class BaseSteps extends BaseUtil {
    protected static String browser;
    private String jobName;

    private StopWatch stopWatch;
    private BaseUtil base;

    private static final Dimension DEFAULT_WINDOW_SIZE = new Dimension(1024, 768);

    private TestNGCucumberRunner testRunner;

    public BaseSteps(BaseUtil base) {
        this.base = base;
    }

    @Before
    public void setUP(Scenario scenario) throws Exception {
        jobName = scenario.getName();
        browser = FrameworkProperties.getBrowser();
        base.driver = WebDriverFactory.getWebDriver(browser);
        base.driver.manage().window().setSize(DEFAULT_WINDOW_SIZE);
        base.driver = new EventFiringWebDriver(base.driver).register(new CustomWebDriverLogger());
        stopWatch = new StopWatch();
        stopWatch.start();
        testRunner = new TestNGCucumberRunner(BaseSteps.class);
    }

    @DataProvider(name="features")
    public Object[][] getFeatures()
    {
        return testRunner.provideFeatures();
    }
    @After
    public void tearDown(Scenario scenario) throws IOException, JSONException {
        base.driver.quit();

        String duration = "";
        if (stopWatch != null) {
            stopWatch.stop();
            duration = stopWatch.toString();
        }

        if (browser.equals("sauce_labs")) {
            String sessionId = scenario.getId();
            SauceUtils.UpdateResults(System.getProperty("sauce_username"), System.getProperty("access_key"), !scenario.isFailed(), sessionId);
            System.out.println("SessionID:" + sessionId + " " + "job-name:" + jobName + " " + "Tested on:" + browser);

        }
        if (scenario.isFailed()) {
            byte[] screenshot = getScreenshot(base.driver);
            scenario.embed(screenshot, "image/png");
        }

        info("Test Case Duration [" + duration + "]");
        info("Test-Case:" + jobName);
        info("Browser:" + browser);
    }

    public byte[] getScreenshot(WebDriver driver) {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }

}
