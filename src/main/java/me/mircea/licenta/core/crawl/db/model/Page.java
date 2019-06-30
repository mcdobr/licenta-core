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
	private ObjectId lastJob;

	public Page() {
		this.id = new ObjectId();
		this.type = PageType.UNKNOWN;
	}

	public Page(final String url, final String referer, final PageType type) {
		this(url, referer, type, Instant.now());
	}

	public Page(final String url, final String referer, final Instant discoveredTime) {
		this(url, referer, PageType.UNKNOWN, discoveredTime);
	}

	public Page(final String url, final String referer, final PageType type, final Instant discoveredTime) {
		this.id = new ObjectId();
		this.url = url;
		this.referer = referer;
		this.type = type;
		this.discoveredTime = discoveredTime;
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

	public ObjectId getLastJob() {
		return lastJob;
	}

	public void setLastJob(ObjectId lastJob) {
		this.lastJob = lastJob;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("Page{");
		sb.append("id=").append(id);
		sb.append(", url='").append(url).append('\'');
		sb.append(", referer='").append(referer).append('\'');
		sb.append(", type=").append(type);
		sb.append(", title='").append(title).append('\'');
		sb.append(", discoveredTime=").append(discoveredTime);
		sb.append(", retrievedTime=").append(retrievedTime);
		sb.append(", lastJob=").append(lastJob);
		sb.append('}');
		return sb.toString();
	}
}
