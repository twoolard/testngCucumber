package utils.properties;

import static utils.LogUtil.info;
import static utils.properties.FrameworkConstants.BROWSER;
import static utils.properties.FrameworkConstants.FRAMEWORK_PROPFILE_NAME;

import java.util.Properties;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import utils.LogUtil;

public class FrameworkProperties {
    private static final Properties frameworkProps = new Properties();
    private static Config conf;
    private static Integer driverTimeOut = -1;

    /**
     * Will return the browser specified as a maven command line argument
     * or in the testConfig.properties file, or FireFox as a default.
     *
     * @return the browser as a string
     */
    public static String getBrowser() {
        String browser = System.getProperty(BROWSER);
        LogUtil.debug("Searching system property for browser, found ["
                + browser + "]");

        if (browser == null || browser.isEmpty()) {
            browser = getProperty(BROWSER);
        }

        LogUtil.debug("Configured Browser: " + browser);
        return browser;
    }

    public static String getProperty(String property) {
        loadProperties(FRAMEWORK_PROPFILE_NAME);
        String value = conf.getString(property);
        info("Retrieved Property: key [" + property + "], value [" + value + "]");

        return (String) conf.getAnyRef(property);
    }

    private static void loadProperties(String propertyFileName) {
        if (frameworkProps.isEmpty()) {
            LogUtil.debug("Attempting to load framework property file: " + propertyFileName);
            conf = ConfigFactory.load("local.framework.properties")
                    .withFallback(ConfigFactory.load(propertyFileName));
        }
    }

    public static int getDefaultDriverTimeout() {
        if (driverTimeOut < 0) {
            driverTimeOut = Integer.parseInt(getProperty(FrameworkConstants.DRIVER_TIMEOUT_DEFAULT));
        }
        return driverTimeOut;
    }

    public static String allowDbUpdates() {
        return getProperty("db.allowupdates");
    }


}
