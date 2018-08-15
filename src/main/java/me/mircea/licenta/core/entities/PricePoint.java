package me.mircea.licenta.core.entities;

import java.time.Instant;

import javax.persistence.*;

@Entity
@Table(name = "pricepoints")
public class PricePoint {
	@Id
	private Integer id;

	private Double nominalValue;
	private Character currency;
	private Instant retrievedTime;

	@ManyToOne
	private Site site;
	
	public PricePoint() {}

	public PricePoint(Integer id, Double nominalValue, Character currency, Instant retrievedTime, Site site) {
		super();
		this.id = id;
		this.nominalValue = nominalValue;
		this.currency = currency;
		this.retrievedTime = retrievedTime;
		this.site = site;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Double getNominalValue() {
		return nominalValue;
	}

	public void setNominalValue(Double nominalValue) {
		this.nominalValue = nominalValue;
	}

	public Character getCurrency() {
		return currency;
	}

	public void setCurrency(Character currency) {
		this.currency = currency;
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
}
