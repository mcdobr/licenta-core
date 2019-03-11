package me.mircea.licenta.core.crawl.db.model;

import java.time.Instant;

import org.bson.types.ObjectId;

public class Page {
	private ObjectId id;
	private String url;
	private String referer;
	private PageType type;
	private String title;
	private Instant discoveredTime;
	private Instant retrievedTime;


	public Page() {
		type = PageType.UNKNOWN;
	}
	
	public Page(final String url, final Instant discoveredTime, final String referer, final PageType type) {
		this.url = url;
		this.discoveredTime = discoveredTime;
		this.referer = referer;
		this.type = type;
	}
	
	public Page(final Page other) {
		this.id = other.id;
		this.url = other.url;
		this.referer = other.referer;
		this.type = other.type;
		this.title = other.title;
		this.discoveredTime = other.discoveredTime;
		this.retrievedTime = other.retrievedTime;
	}
	
	public Page(final String url, final Instant discoveredTime, final String referer) {
		this(url, discoveredTime, referer, PageType.UNKNOWN);
	}
	
	public ObjectId getId() {
		return id;
	}
	
	public void setId(ObjectId id) {
		this.id = id;
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getReferer() {
		return referer;
	}

	public void setReferer(String referer) {
		this.referer = referer;
	}
	
	public PageType getType() {
		return type;
	}

	public void setType(PageType type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}

	public Instant getDiscoveredTime() {
		return discoveredTime;
	}

	public void setDiscoveredTime(Instant discoveredTime) {
		this.discoveredTime = discoveredTime;
	}

	public Instant getRetrievedTime() {
		return retrievedTime;
	}

	public void setRetrievedTime(Instant retrievedTime) {
		this.retrievedTime = retrievedTime;
	}
}
