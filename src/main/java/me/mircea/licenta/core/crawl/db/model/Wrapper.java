package me.mircea.licenta.core.crawl.db.model;

import com.google.common.base.Preconditions;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Wrapper {
    private ObjectId id;
    private String domain;
    private List<Selector> selectors;

    public Wrapper() {
        this.selectors = new ArrayList<>();
    }

    public Optional<Selector> getSelectorByName(String name) {
        Preconditions.checkNotNull(name);

        Selector searched = null;
        for (Selector s : this.selectors) {
            if (s.getName().equals(name)) {
                searched = s;
                break;
            }
        }

        return Optional.ofNullable(searched);
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
