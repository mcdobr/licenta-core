package me.mircea.licenta.core.crawl.db.model;

public enum PageType {
	PRODUCT("product"),
	SHELF("shelf"),
	JUNK("junk"),
	UNKNOWN("unknown");
	
	String type;
	PageType(String type) {
		this.type = type;
	}
}
