package utils;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import utils.browser.BrowserDimension;
import utils.properties.FrameworkProperties;


public class WebDriverUtil {

    public static void setDriverTimeoutToDefault(WebDriver driver) {
        int timeout = FrameworkProperties.getDefaultDriverTimeout();
        setDriverTimeout(driver, timeout);
    }

    public static void basicDriverSetup(WebDriver driver, BrowserDimension dimension) {
        if (BrowserDimension.MOBILE == dimension) {
            driver.manage().window().setSize(new Dimension(480, 800));
        } else {
            if (!(driver instanceof ChromeDriver)) {
                driver.manage().window().setSize(new Dimension(1024, 768));
            }
        }
        driver.manage().deleteAllCookies();

        driver.manage().timeouts().pageLoadTimeout(120, TimeUnit.SECONDS);
        driver.manage().timeouts().setScriptTimeout(120, TimeUnit.SECONDS);

        setDriverTimeoutToDefault(driver);
    }

    public static void setDriverTimeout(WebDriver driver, int timeOutSeconds) {
        driver.manage().timeouts().implicitlyWait(timeOutSeconds, TimeUnit.SECONDS);
    }
}
