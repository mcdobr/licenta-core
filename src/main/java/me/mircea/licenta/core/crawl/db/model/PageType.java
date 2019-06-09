package me.mircea.licenta.core.crawl.db.model;

public enum PageType {
	PRODUCT("product"),
	SHELF("shelf"),
	JUNK("junk"),
	UNAVAILABLE("unavailable"),
	UNKNOWN("unknown"),
	UNREACHABLE("unreachable");
	
	String type;
	PageType(String type) {
		this.type = type.trim().toLowerCase();
	}
}
