package utils;

import static utils.LogUtil.error;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;



import utils.browser.BrowserDimension;
import utils.browser.BrowserUtil;


public class WebDriverFactory {

    public static final String CHROME = "chrome";
    public static final String SAUCE = "sauce_labs";
    public static final String CHROME_OSX = "chrome_osx";
    public static final String CHROME_MOBILE = "chrome_mobile";
    public static final String FF = "ff";
    public static final String FIREFOX = "FIREFOX";
    public static final String FIREFOX_MOBILE = "firefox_mobile";
    public static final String IE = "ie";
    public static final String IEXPLORER = "iexplorer";
    public static final String INTERNET_EXPLORER = "internetexplorer";
    public static final String PHANTOMJS = "phantomjs";
    public static final String PHANTOMJS_MOBILE = "phantomjs_mobile";
    public static final String OWASP_ZAP_CHROME = "owasp_chrome";
    public static final String OWASP_ZAP_FIREFOX = "owasp_firefox";

    public static WebDriver getWebDriver(String browser) throws Exception {

        WebDriver driver = null;
        BrowserDimension dimension = BrowserDimension.DESKTOP;

        int attempts = 0;
        while (driver == null && attempts < 2) {
            attempts++;
            try {
                if (browser == null || browser.isEmpty())
//                    Preconditions.checkArgument(StringUtils.isNotEmpty(browser));
                    throw new Exception("No browser is defined.");

                if (CHROME.equalsIgnoreCase(browser)) {
                    driver = BrowserUtil.setupChrome(true);
                } else if (CHROME_OSX.equalsIgnoreCase(browser)) {
                    driver = BrowserUtil.setupChrome(false);
                } else if (CHROME_MOBILE.equalsIgnoreCase(browser)) {
                    dimension = BrowserDimension.MOBILE;
                    driver = BrowserUtil.setupChrome(true);
                } else if (FF.equalsIgnoreCase(browser) ||
                        FIREFOX.equalsIgnoreCase(browser)) {
                    driver = BrowserUtil.setupFireFox();
                } else if (FIREFOX_MOBILE.equalsIgnoreCase(browser)) {
                    dimension = BrowserDimension.MOBILE;
                    driver = BrowserUtil.setupFireFox();
                } else if (IE.equalsIgnoreCase(browser) ||
                        IEXPLORER.equalsIgnoreCase(browser) ||
                        INTERNET_EXPLORER.equalsIgnoreCase(browser)) {
                    driver = BrowserUtil.setupIE();
                } else if (PHANTOMJS.equalsIgnoreCase(browser)) {
                    driver = BrowserUtil.setupPhantomJs();
                } else if (PHANTOMJS_MOBILE.equalsIgnoreCase(browser)) {
                    dimension = BrowserDimension.MOBILE;
                    driver = BrowserUtil.setupPhantomJs();
                } else if (SAUCE.equalsIgnoreCase(browser)) {
                        driver = BrowserUtil.setupSauceLabs();
                } else {
                    throw new Exception("Browser [" + browser + "] is not supported.");
                }
            } catch (WebDriverException e) {
                try {
                    error("First exception: => " + e.getMessage(), e);
                    driver.quit();
                } catch (Exception e2) {
                    error("Second exception: => " + e2.getMessage(), e2);
                }
                driver = null;
            }
        }
        if (driver == null) throw new RuntimeException("Error initiating WebDriver!");
        WebDriverUtil.basicDriverSetup(driver, dimension);
        return driver;
    }
}
