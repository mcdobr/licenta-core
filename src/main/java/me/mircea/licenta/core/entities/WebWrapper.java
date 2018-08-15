package me.mircea.licenta.core.entities;

import javax.persistence.*;

@Entity
@Table(name="webwrappers")
public class WebWrapper {
	@Id
	private Integer id;
	private String titleSelector;
	private String priceSelector;
	private String authorsSelector;
	private String descriptionSelector;
	private String imageLinkSelector;
	private String paginationLinkSelector;

	public WebWrapper() {}

	public WebWrapper(Integer id, String titleSelector, String priceSelector, String authorsSelector,
			String descriptionSelector, String imageLinkSelector, String paginationLinkSelector) {
		super();
		this.id = id;
		this.titleSelector = titleSelector;
		this.priceSelector = priceSelector;
		this.authorsSelector = authorsSelector;
		this.descriptionSelector = descriptionSelector;
		this.imageLinkSelector = imageLinkSelector;
		this.paginationLinkSelector = paginationLinkSelector;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitleSelector() {
		return titleSelector;
	}

	public void setTitleSelector(String titleSelector) {
		this.titleSelector = titleSelector;
	}

	public String getPriceSelector() {
		return priceSelector;
	}

	public void setPriceSelector(String priceSelector) {
		this.priceSelector = priceSelector;
	}

	public String getAuthorsSelector() {
		return authorsSelector;
	}

	public void setAuthorsSelector(String authorsSelector) {
		this.authorsSelector = authorsSelector;
	}

	public String getDescriptionSelector() {
		return descriptionSelector;
	}

	public void setDescriptionSelector(String descriptionSelector) {
		this.descriptionSelector = descriptionSelector;
	}

	public String getImageLinkSelector() {
		return imageLinkSelector;
	}

	public void setImageLinkSelector(String imageLinkSelector) {
		this.imageLinkSelector = imageLinkSelector;
	}

	public String getPaginationLinkSelector() {
		return paginationLinkSelector;
	}

	public void setPaginationLinkSelector(String paginationLinkSelector) {
		this.paginationLinkSelector = paginationLinkSelector;
	}
}
