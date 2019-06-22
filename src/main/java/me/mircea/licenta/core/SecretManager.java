package me.mircea.licenta.core;

import me.mircea.licenta.core.crawl.db.model.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SecretManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(SecretManager.class);
    public static final SecretManager instance = new SecretManager();

    private static final Properties secretProperties;

    static {
        final String secretFile = "secret.properties";
        final InputStream secretInputStream = Job.class.getResourceAsStream("/" + secretFile);

        secretProperties = new Properties();
        try {
            secretProperties.load(secretInputStream);
        } catch (IOException e) {
            LOGGER.error("Could not find the secret file {}. EXITING", e);
            System.exit(-1);
        }
    }

    public String getCrawlDbEndpoint() {
        return secretProperties.getProperty("crawl_db_connection_string");
    }

    public String getManagerEndpoint() {
        return secretProperties.getProperty("manager_whitelist");
    }

    public String getCrawlerEndpoint() {
        return secretProperties.getProperty("crawler_endpoint");
    }

    public String getScraperEndpoint() {
        return secretProperties.getProperty("scraper_endpoint");
    }
}
