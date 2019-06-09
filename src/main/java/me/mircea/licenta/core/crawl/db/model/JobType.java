package me.mircea.licenta.core.crawl.db.model;

public enum JobType {
    CRAWL("crawl"),
    SCRAPE("scrape");

    String type;
    JobType(String type) {
        this.type = type.trim().toLowerCase();
    }
}
