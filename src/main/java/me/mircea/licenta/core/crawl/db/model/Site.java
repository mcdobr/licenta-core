package me.mircea.licenta.core.crawl.db.model;

import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class Site {
    private ObjectId id;
    private String domain;
    private String homepage;
    private List<String> seeds;
    private boolean disallowCookies;

    public Site() {
        this.seeds = new ArrayList<>();
        this.disallowCookies = false;
    }

    public ObjectId getId() {
        return id;
    }

    public String getDomain() {
        return domain;
    }

    public String getHomepage() {
        return homepage;
    }

    public List<String> getSeeds() {
        return seeds;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public void setSeeds(List<String> seeds) {
        this.seeds = seeds;
    }

    public boolean getDisallowCookies() {
        return disallowCookies;
    }

    public void setDisallowCookies(boolean disallowCookies) {
        this.disallowCookies = disallowCookies;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Site{");
        sb.append("id=").append(id);
        sb.append(", domain='").append(domain).append('\'');
        sb.append(", homepage='").append(homepage).append('\'');
        sb.append(", seeds=").append(seeds).append('\'');
        sb.append(", disallowCookies=").append(disallowCookies);
        sb.append('}');
        return sb.toString();
    }
}
