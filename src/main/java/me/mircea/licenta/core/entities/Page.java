package me.mircea.licenta.core.entities;

import java.time.Instant;

import javax.persistence.*;

@Entity
@Table(name = "pages")
public class Page {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String url;
	private Integer type;
	private Instant discoveredTime;
	private Instant retrievedTime;

	@ManyToOne
	private Site site;

	@OneToOne
	private Book book;

	public Page() {
	}

	public Page(Integer id, String url, Integer type, Instant discoveredTime, Instant retrievedTime, Site site,
			Book book) {
		super();
		this.id = id;
		this.url = url;
		this.type = type;
		this.discoveredTime = discoveredTime;
		this.retrievedTime = retrievedTime;
		this.site = site;
		this.book = book;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
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

	public Site getSite() {
		return site;
	}

	public void setSite(Site site) {
		this.site = site;
	}

	public Book getBook() {
		return book;
	}

	public void setBook(Book book) {
		this.book = book;
	}
}
