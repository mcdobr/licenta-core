package me.mircea.licenta.core.entities;

import java.net.MalformedURLException;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.google.common.base.Preconditions;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import me.mircea.licenta.core.utils.HtmlUtil;

@Entity
@javax.persistence.Entity
@Table(name = "sites")
public class Site {
	@Id
	@javax.persistence.Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private String url;
	
	@OneToOne
	//TODO: add wrapper to toString, constructors, hashcode etc?
	private WebWrapper wrapper;
	
	
	public Site() {
	}

	public Site(Long id, String name, String url) {
		super();
		
		Preconditions.checkNotNull(name);
		Preconditions.checkNotNull(url);
		
		this.id = id;
		this.name = name;
		this.url = url;
	}
	
	public Site(String url) throws MalformedURLException {
		this(null, HtmlUtil.getDomainOfUrl(url), url);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public WebWrapper getWrapper() {
		return wrapper;
	}

	public void setWrapper(WebWrapper wrapper) {
		this.wrapper = wrapper;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Site [id=" + id + ", name=" + name + ", url=" + url + "]";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Site))
			return false;
		Site other = (Site) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}
}