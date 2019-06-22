package me.mircea.licenta.core.crawl.db.model;

import com.google.common.collect.ImmutableMap;
import org.bson.types.ObjectId;

import java.util.Map;

public class Session {
    private final ObjectId id;
    private final ImmutableMap<String, ObjectId> startedJobs;

    public Session(Map<String, ObjectId> startedJobs) {
        this.id = new ObjectId();
        this.startedJobs = ImmutableMap.copyOf(startedJobs);
    }

}
