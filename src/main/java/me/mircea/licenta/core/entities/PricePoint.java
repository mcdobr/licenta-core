package me.mircea.licenta.core.entities;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;

import javax.persistence.*;

@Entity
@Table(name = "pricepoints")
public class PricePoint {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private BigDecimal nominalValue;
	private Currency currency;
	private Instant retrievedTime;

	@ManyToOne
	private Site site;
	
	public PricePoint() {}

	public PricePoint(Integer id, BigDecimal nominalValue, Currency currency, Instant retrievedTime, Site site) {
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

	public BigDecimal getNominalValue() {
		return nominalValue;
	}

	public void setNominalValue(BigDecimal nominalValue) {
		this.nominalValue = nominalValue;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
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
