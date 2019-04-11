package me.mircea.licenta.core.crawl.db.model;

public enum SelectorType {
    TEXT("text"),
    LINK("link"),
    IMAGE("image"),
    ELEMENT("element");

    String type;
    SelectorType(String type) {
        this.type = type;
    }
}
