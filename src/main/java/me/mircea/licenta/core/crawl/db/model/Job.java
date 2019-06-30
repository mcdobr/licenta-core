package me.mircea.licenta.core.crawl.db.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import crawlercommons.robots.BaseRobotRules;
import crawlercommons.robots.SimpleRobotRules;
import crawlercommons.robots.SimpleRobotRulesParser;
import me.mircea.licenta.core.crawl.db.CrawlDatabaseManager;
import me.mircea.licenta.core.crawl.db.RobotDefaults;
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
import java.util.*;

public class Job {
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;
    private Collection<String> seeds;
    private String domain;
    private JobStatus status;
    private JobType type;
    private Instant start;
    private Instant end;

    @JsonIgnore
    private BaseRobotRules robotRules;

    public Job() {
        this.id = new ObjectId();
    }

    public Job(String homepage, JobType type) throws IOException {
        this(homepage, type, Collections.emptyList());
    }

    public Job(String homepage, JobType type, String seed) throws IOException {
        this(homepage, type, Collections.singletonList(seed));
    }

    public Job(String homepage, JobType type, ObjectId jobIdToBeContinued) throws IOException {
        this(homepage, type, Collections.emptyList(), jobIdToBeContinued);
    }

    public Job(String homepage, JobType type, Collection<String> seeds, ObjectId jobIdToBeContinued) throws IOException {
        this(homepage, type, seeds);
        this.id = jobIdToBeContinued;
    }

    public Job(String homepage, JobType type, Collection<String> seeds) throws IOException {
        this(homepage, type, seeds, Collections.emptyList());
    }

    public Job(String homepage, JobType type, Collection<String> seeds, Iterable<String> sitemaps) throws IOException {
        this.id = new ObjectId();
        this.domain = HtmlUtil.getDomainOfUrl(homepage);
        this.seeds = seeds;
        this.type = type;
        this.start = Instant.now();
        this.status = JobStatus.ACTIVE;
        this.robotRules = readRobotsFile(homepage, RobotDefaults.getDefaults());
        for (String sitemap : sitemaps) {
            this.robotRules.addSitemap(sitemap);
        }

        if (CrawlDatabaseManager.instance.isThereAnyJobRunningOnDomain(this.domain)) {
            throw new JobActiveOnHost("Could not start job of type " + type +
                    "because a job is still active on host " + HtmlUtil.getDomainOfUrl(homepage));
        }
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
        return actualUrl.getProtocol() + "://" + actualUrl.getAuthority() + "/robots.txt";
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public Collection<String> getSeeds() {
        return seeds;
    }

    public void setSeeds(Collection<String> seeds) {
        this.seeds = seeds;
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
