package me.mircea.licenta.core.crawl;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import crawlercommons.robots.BaseRobotRules;
import crawlercommons.robots.SimpleRobotRules;
import crawlercommons.robots.SimpleRobotRulesParser;
import me.mircea.licenta.core.parser.utils.HtmlUtil;

public class CrawlRequest {
	private static final String PROPERTIES_FILENAME = "crawler_default.properties";
	private static final InputStream propertiesInputStream = CrawlRequest.class.getResourceAsStream("/" + PROPERTIES_FILENAME);
	
	private String startUrl;
	private String domain;
	private Map<String, String> properties;
	private BaseRobotRules robotRules;

	public CrawlRequest(String startUrl) throws IOException {
		this.startUrl = startUrl;
		this.domain = HtmlUtil.getDomainOfUrl(startUrl);
		this.properties = readPropertiesFile(propertiesInputStream);
		this.robotRules = readRobotsFile(startUrl, properties);
	}
	
	private Map<String, String> readPropertiesFile(InputStream resourceLocation) throws IOException {
		Map<String, String> props = new HashMap<>();
		
		Properties persistedProps = new Properties();
		persistedProps.load(resourceLocation);
		persistedProps.forEach((key, value) -> 
			props.put(key.toString(), value.toString()));
		
		return props;
	}
	
	/**
	 * If no robots file found, then just set the crawl-delay to a conservative default
	 */
	private BaseRobotRules readRobotsFile(final String startUrl, final Map<String, String> properties) throws IOException {
		BaseRobotRules rules;
		final String robotsUrl = getRobotsFileUrl(startUrl);
		
		HttpURLConnection connection = (HttpURLConnection) new URL(robotsUrl).openConnection();
		connection.setRequestProperty("User-Agent", properties.get("user_agent"));
		SimpleRobotRulesParser ruleParser = new SimpleRobotRulesParser();
		
		if (connection.getResponseCode() != 200) {
			rules = new SimpleRobotRules();
		} else {
			byte[] content = IOUtils.toByteArray(connection);
			rules = ruleParser.parseContent(robotsUrl, content, "text/plain", properties.get("bot_name"));
		}

		final long defaultCrawlDelay = Long.parseLong(properties.get("default_crawl_delay"));
		if (rules.getCrawlDelay() == BaseRobotRules.UNSET_CRAWL_DELAY) {
			rules.setCrawlDelay(defaultCrawlDelay);
		}
		
		return rules;
	}
	
	private String getRobotsFileUrl(final String startUrl) throws MalformedURLException {
		URL actualUrl = new URL(startUrl);
		return actualUrl.getProtocol()+ "://" + actualUrl.getAuthority() + "/robots.txt";
	}

	public String getStartUrl() {
		return startUrl;
	}
	
	public String getDomain() {
		return domain;
	}
	
	public BaseRobotRules getRobotRules() {
		return robotRules;
	}
	
	public Map<String, String> getProperties() {
		return properties;
	}
}
