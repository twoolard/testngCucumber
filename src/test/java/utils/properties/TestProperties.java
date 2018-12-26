package utils.properties;
import static utils.LogUtil.info;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class TestProperties {

    private static Config conf;
    private static String PROP_FILE_ARG = FrameworkConstants.TEST_PROPFILE_ARG;

    public static void loadTestParameters() {
        if (conf == null) {
            String propFile = (System.getProperty(PROP_FILE_ARG) == null) ? "test.properties" : System.getProperty(PROP_FILE_ARG);
            conf = ConfigFactory.load("local.test.properties")
                    .withFallback(ConfigFactory.load(propFile));

            // Don't print this out unless debugging as ID/PWD for sensitive information can be printed
            // debug(conf.toString());
        }
    }

    public static String getProperty(String paramName) {
        loadTestParameters();
        String value = conf.getString(paramName);
        if (paramName != null && !paramName.toLowerCase().contains("pwd") && !paramName.toLowerCase().contains("password")) {
            info("Retrieved Property: key [" + paramName + "], value [" + value + "]");
        } else {
            info("Retrieved Property: key [" + paramName + "], value [DON'T SHOW PASSWORDS]");
        }

        return (String) conf.getAnyRef(paramName);
    }

    public static Config getConfigSection(String sectionName) {
        return conf.getConfig(sectionName);
    }
}