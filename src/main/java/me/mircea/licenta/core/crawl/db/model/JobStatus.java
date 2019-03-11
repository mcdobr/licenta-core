package me.mircea.licenta.core.crawl.db.model;

public enum JobStatus {
    ACTIVE("active"),
    FINISHED("finished");

    String type;
    JobStatus(String type) {
        this.type = type;
    }
}
