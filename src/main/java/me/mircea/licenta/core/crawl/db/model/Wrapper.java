package me.mircea.licenta.core.crawl.db.model;

import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class Wrapper {
    private ObjectId id;
    private String domain;
    private List<Selector> selectors;

    public Wrapper() {
        this.selectors = new ArrayList<>();
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public List<Selector> getSelectors() {
        return selectors;
    }

    public void setSelectors(List<Selector> selectors) {
        this.selectors = selectors;
    }
}
