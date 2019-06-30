package me.mircea.licenta.core.crawl.db.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.google.common.collect.ImmutableMap;
import org.bson.types.ObjectId;

import java.util.Map;

public class Session {
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;
    private ImmutableMap<String, ObjectId> startedJobs;

    public Session() {}

    public Session(Map<String, ObjectId> startedJobs) {
        this.id = new ObjectId();
        this.startedJobs = ImmutableMap.copyOf(startedJobs);
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public ImmutableMap<String, ObjectId> getStartedJobs() {
        return startedJobs;
    }

    public void setStartedJobs(Map<String, ObjectId> startedJobs) {
        this.startedJobs = ImmutableMap.copyOf(startedJobs);
    }
}
