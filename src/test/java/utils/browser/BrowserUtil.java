package utils.browser;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Level;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import utils.LogUtil;


public class BrowserUtil {
    private static BrowserType browserType;
    private static ChromeDriverService chromeService;

    private static final String DOWNLOAD_DEFAULT_DIRECTORY = "download.default_directory";

    /**
     * Performs some Chrome specific setup by enabling certain capabilities.
     *
     * @return
     * @throws IOException
     */
    public static WebDriver setupChrome(boolean isWin) throws IOException {
        browserType = BrowserType.CHROME;

        setChromeDriverPath(isWin);
        ChromeOptions options = setChromeOptions();
        DesiredCapabilities capabilities = getGenericDesiredCapabilities(false, true, true, true);
        capabilities.setCapability(ChromeOptions.CAPABILITY, options);
        WebDriver driver = new ChromeDriver(options);
        return driver;
    }

    private static void setChromeDriverPath(boolean isWin) {
        // Need to ensure the path to the ChromeDriver executable is set
        if (isChromeDriverExePropertySet()) {
            String driverPath = System.getProperty(ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY).replace("/", File.separator);
            System.setProperty(ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY, driverPath);
        } else {
            String osxExtra = "";
            if (!isWin) {
                osxExtra = "-osx";
            }
            StringBuilder sb = new StringBuilder();
            sb.append(System.getProperty("user.dir"))
                    .append(File.separator)
                    .append("src,test,resources,drivers,chrome,chromedriver" + osxExtra + ".exe");
            System.setProperty(ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY, sb.toString().replace(",", File.separator));
        }
    }

    private static boolean isChromeDriverExePropertySet() {
        return Objects.nonNull(System.getProperty(ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY));
    }

    public static void cleanUpChrome() {
        chromeService.stop();
    }

    /**
     * Performs some Firefox specific setup by enabling certain capabilities.
     *
     * @return
     */
    public static WebDriver setupFireFox() {
        browserType = BrowserType.FIREFOX;

        FirefoxOptions options = new FirefoxOptions();

        DesiredCapabilities capabilities = DesiredCapabilities.firefox();
        capabilities.setCapability("moz:firefoxOptions", options);

        FirefoxDriver driver = new FirefoxDriver(capabilities);
        return driver;
    }

    public static WebDriver setupPhantomJs() {
        if (System.getProperty("phantomjs.binary.path") == null) {
            StringBuilder sb = new StringBuilder();
            sb.append(System.getProperty("user.dir"))
                    .append(File.separator)
                    .append("src,test,resources,drivers,phantomjs,phantomjs.exe");
            System.setProperty("phantomjs.binary.path", sb.toString().replace(",", File.separator));
        }

        DesiredCapabilities caps = getGenericDesiredCapabilities();
        caps.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, new String[]{"--web-security=no", "--ignore-ssl-errors=yes"});
        WebDriver driver = new PhantomJSDriver(caps);
        return driver;
    }

    /**
     * Performs some IE specific setup by enabling certain capabilities.
     *
     * @return
     */
    public static WebDriver setupIE() {
        setIEDriverPath();

        try {
            Runtime.getRuntime().exec("RunDll32.exe InetCpl.cpl,ClearMyTracksByProcess 2");
            Runtime.getRuntime().exec("RunDll32.exe InetCpl.cpl,ClearMyTracksByProcess 2");
            // Clear Temp Internet Files
            Runtime.getRuntime().exec("RunDll32.exe InetCpl.cpl,ClearMyTracksByProcess 8");

            Thread.sleep(10000);
        } catch (Exception e) {
            LogUtil.warn("WARN - Failed to clear IE cache.");
        }

        browserType = BrowserType.IE;
        DesiredCapabilities capabilites = getGenericDesiredCapabilities();
        DesiredCapabilities merged = capabilites.merge(getIEDesiredCapabilities());
        WebDriver driver = new InternetExplorerDriver(merged);
        return driver;
    }

    private static void setIEDriverPath() {
        if (System.getProperty("webdriver.ie.driver") == null) {
            StringBuilder sb = new StringBuilder();
            sb.append(System.getProperty("user.dir"))
                    .append(File.separator)
                    .append("src,test,resources,drivers,iexplorer,IEDriverServer.exe");
            System.setProperty("webdriver.ie.driver", sb.toString().replace(",", File.separator));
        }
    }

    /**
     * Capabilities generic across all browser types
     *
     * @return
     */
    private static DesiredCapabilities getGenericDesiredCapabilities() {
        return getGenericDesiredCapabilities(true, true, true, true);
    }

    private static DesiredCapabilities getGenericDesiredCapabilities(boolean acceptSSLCerts, boolean supportJavascript, boolean nativeEvents, boolean takeScreenshot) {
        DesiredCapabilities dc = new DesiredCapabilities();

        dc.setCapability(CapabilityType.ACCEPT_SSL_CERTS, acceptSSLCerts);
        dc.setCapability(CapabilityType.SUPPORTS_JAVASCRIPT, supportJavascript);
        dc.setCapability(CapabilityType.HAS_NATIVE_EVENTS, nativeEvents);
        dc.setCapability(CapabilityType.TAKES_SCREENSHOT, takeScreenshot);
        dc.setCapability(CapabilityType.LOGGING_PREFS, getGenericLoggingPreferences());

        return dc;
    }

    private static LoggingPreferences getGenericLoggingPreferences() {
        LoggingPreferences logs = new LoggingPreferences();
        logs.enable(LogType.BROWSER, Level.SEVERE);
        logs.enable(LogType.CLIENT, Level.WARNING);
        logs.enable(LogType.DRIVER, Level.WARNING);
        logs.enable(LogType.PERFORMANCE, Level.OFF);
        logs.enable(LogType.SERVER, Level.SEVERE);
        return logs;
    }

    /**
     * IE specific capabilities
     *
     * @return
     */
    private static DesiredCapabilities getIEDesiredCapabilities() {
        DesiredCapabilities dc = new DesiredCapabilities();

        dc.setCapability("ignoreProtectedModeSettings", true);
        dc.setCapability("ignoreZoomSetting", true);
        dc.setCapability("requireWindowFocus", true);
        dc.setCapability("enablePersistentHover", true);
        dc.setCapability("browserAttachTimeout", 60000);

        return dc;
    }

    /**
     * Chrome specific options (typically this is Chrome switches)
     *
     * @return
     */
    private static ChromeOptions setChromeOptions() {
        ChromeOptions options = new ChromeOptions();

        HashMap<String, Object> chromePrefs = new HashMap<String, Object>();

        if (Objects.nonNull(System.getProperty(DOWNLOAD_DEFAULT_DIRECTORY))) {
            Path path = Paths.get(System.getProperty(DOWNLOAD_DEFAULT_DIRECTORY));
            String downloadPath = path.toAbsolutePath().toString();

            System.setProperty(DOWNLOAD_DEFAULT_DIRECTORY, downloadPath);

            chromePrefs.put("profile.default_content_settings.popups", 0);
            chromePrefs.put(DOWNLOAD_DEFAULT_DIRECTORY, downloadPath);
        }

        options.setExperimentalOption("prefs", chromePrefs);

        options.addArguments("disable-extensions");
        options.addArguments("no-sandbox");
        options.addArguments("start-maximized");
        options.addArguments("bwsi");

        return options;
    }

    public static BrowserType getCurrentBrowserType() {
        return browserType;
    }

    public static WebDriver setupSauceLabs() throws Exception {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("platform", System.getProperty("platform"));
        caps.setCapability("browserName", System.getProperty("browserName"));
        caps.setCapability("version", System.getProperty("version"));
        caps.setCapability("parentTunnel", System.getProperty("parentTunnel"));
        caps.setCapability("tunnelIdentifier", System.getProperty("tunnelIdentifier"));
        return new RemoteWebDriver(getSauceLabsURL(), caps);
    }

    private static URL getSauceLabsURL() throws MalformedURLException {
        if (System.getProperty("access_key") == null || System.getProperty("sauce_username") == null)
            throw new Error("No sauce labs credentials have been defined");

        return new URL("https://" + System.getProperty("sauce_username") + ":" + System.getProperty("access_key")+ "@ondemand.saucelabs.com:443/wd/hub");
    }

}

