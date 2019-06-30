package me.mircea.licenta.core.crawl.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author mircea
 * Static utility class that reads robot properties.
 */
public final class RobotDefaults {
    private static final Logger LOGGER = LoggerFactory.getLogger(RobotDefaults.class);
    private static final String CONFIG_FILE_USER_AGENT_KEY = "user_agent";

    private static Map<String, String> defaultProperties;

    static {
        try {
            final String BOT_DEFAULT_PROPS_FILE = "botDefault.properties";
            final InputStream botDefaultPropsInputStream = RobotDefaults.class.getResourceAsStream("/" + BOT_DEFAULT_PROPS_FILE);

            defaultProperties = new HashMap<>();
            Properties persistedProps = new Properties();
            persistedProps.load(botDefaultPropsInputStream);
            persistedProps.forEach((key, value) ->
                    defaultProperties.put(key.toString(), value.toString()));
        } catch (IOException e) {
            LOGGER.error("Fatal error: Could not open bot default file {}", e);
            System.exit(-1);
        }
    }

    private RobotDefaults() {
    }

    public static Map<String, String> getDefaults() {
        return defaultProperties;
    }

    public static String getDefault(String key) {
        return defaultProperties.get(key);
    }

    public static String getUserAgent() {
        return defaultProperties.get(CONFIG_FILE_USER_AGENT_KEY);
    }
}
