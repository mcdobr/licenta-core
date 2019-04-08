package me.mircea.licenta.core.crawl.db.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import crawlercommons.robots.BaseRobotRules;
import crawlercommons.robots.SimpleRobotRules;
import crawlercommons.robots.SimpleRobotRulesParser;
import me.mircea.licenta.core.parser.utils.HtmlUtil;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Job {
    private static final Logger LOGGER = LoggerFactory.getLogger(Job.class);
    private static Map<String, String> properties;

    static {
        try {
            final String BOT_DEFAULT_PROPS_FILE = "botDefault.properties";
            final InputStream botDefaultPropsInputStream = Job.class.getResourceAsStream("/" + BOT_DEFAULT_PROPS_FILE);

            properties = new HashMap<>();
            Properties persistedProps = new Properties();
            persistedProps.load(botDefaultPropsInputStream);
            persistedProps.forEach((key, value) ->
                    properties.put(key.toString(), value.toString()));
        } catch (IOException e) {
            LOGGER.error("Fatal error: Could not open bot default file {}", e);
            System.exit(-1);
        }
    }

    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;
    private String seed;
    private String domain;
    private JobStatus status;
    private JobType type;
    private Instant start;
    private Instant end;
    private BaseRobotRules robotRules;

    public Job() {
        this.id = new ObjectId();
    }

    public Job(ObjectId continueJob, String seed, JobType type) throws IOException {
        this(seed, type);
        this.id = continueJob;
    }


    public Job(String seed, JobType type) throws IOException {
        this.id = new ObjectId();
        this.seed = seed;
        this.domain = HtmlUtil.getDomainOfUrl(seed);
        this.type = type;
        this.start = Instant.now();
        this.status = JobStatus.ACTIVE;
        this.robotRules = readRobotsFile(seed, properties);
    }

    public static String getDefault(String key) {
        return properties.get(key);
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

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getSeed() {
        return seed;
    }

    public void setSeed(String seed) {
        this.seed = seed;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }

    public JobType getType() {
        return type;
    }

    public void setType(JobType type) {
        this.type = type;
    }

    public Instant getStart() {
        return start;
    }

    public void setStart(Instant start) {
        this.start = start;
    }

    public Instant getEnd() {
        return end;
    }

    public void setEnd(Instant end) {
        this.end = end;
    }

    public BaseRobotRules getRobotRules() {
        return robotRules;
    }

    public void setRobotRules(BaseRobotRules robotRules) {
        this.robotRules = robotRules;
    }
}
