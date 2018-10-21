package me.mircea.licenta.core.entities;

import javax.persistence.*;

@Entity
@Table(name = "webwrappers")
public class WebWrapper {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String titleSelector;
	private String authorsSelector;
	private String priceSelector;
	private String attributeSelector;
	private String descriptionSelector;
	private String imageLinkSelector;

	public WebWrapper() {
	}

	public WebWrapper(Integer id, String titleSelector, String authorsSelector, String priceSelector,
			String attributeSelector, String descriptionSelector, String imageLinkSelector) {
		super();
		this.id = id;
		this.titleSelector = titleSelector;
		this.authorsSelector = authorsSelector;
		this.priceSelector = priceSelector;
		this.attributeSelector = attributeSelector;
		this.descriptionSelector = descriptionSelector;
		this.imageLinkSelector = imageLinkSelector;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("WebWrapper [id=").append(id);
		builder.append(", titleSelector=").append(titleSelector);
		builder.append(", authorsSelector=").append(authorsSelector);
		builder.append(", priceSelector=").append(priceSelector);
		builder.append(", attributeSelector=").append(attributeSelector);
		builder.append(", descriptionSelector=").append(descriptionSelector);
		builder.append(", imageLinkSelector=").append(imageLinkSelector);
		builder.append("]");
		return builder.toString();
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

	public String getAuthorsSelector() {
		return authorsSelector;
	}

	public void setAuthorsSelector(String authorsSelector) {
		this.authorsSelector = authorsSelector;
	}

	public String getPriceSelector() {
		return priceSelector;
	}

	public void setPriceSelector(String priceSelector) {
		this.priceSelector = priceSelector;
	}

	public String getAttributeSelector() {
		return attributeSelector;
	}

	public void setAttributeSelector(String attributeSelector) {
		this.attributeSelector = attributeSelector;
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
}
