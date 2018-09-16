package me.mircea.licenta.core.entities;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Currency;
import java.util.Locale;

import javax.persistence.*;

@Entity
@Table(name = "pricepoints")
public class PricePoint {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private BigDecimal nominalValue;
	private Currency currency;
	private LocalDate retrievedDay;

	@ManyToOne
	private Site site;

	public PricePoint() {
	}

	public PricePoint(Integer id, BigDecimal nominalValue, Currency currency, LocalDate retrievedDay, Site site) {
		super();
		this.id = id;
		this.nominalValue = nominalValue;
		this.currency = currency;
		this.retrievedDay = retrievedDay;
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

	public LocalDate getRetrievedTime() {
		return retrievedDay;
	}

	public void setRetrievedTime(LocalDate retrievedTime) {
		this.retrievedDay = retrievedTime;
	}

	public Site getSite() {
		return site;
	}

	public void setSite(Site site) {
		this.site = site;
	}
	
	@Override
	public String toString() {
		return "PricePoint [id=" + id + ", nominalValue=" + nominalValue + ", currency=" + currency + ", retrievedDay="
				+ retrievedDay + ", site=" + site + "]";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((currency == null) ? 0 : currency.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((nominalValue == null) ? 0 : nominalValue.hashCode());
		result = prime * result + ((retrievedDay == null) ? 0 : retrievedDay.hashCode());
		result = prime * result + ((site == null) ? 0 : site.hashCode());
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
		if (!(obj instanceof PricePoint))
			return false;
		PricePoint other = (PricePoint) obj;
		if (currency == null) {
			if (other.currency != null)
				return false;
		} else if (!currency.equals(other.currency))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (nominalValue == null) {
			if (other.nominalValue != null)
				return false;
		} else if (!nominalValue.equals(other.nominalValue))
			return false;
		if (retrievedDay == null) {
			if (other.retrievedDay != null)
				return false;
		} else if (!retrievedDay.equals(other.retrievedDay))
			return false;
		if (site == null) {
			if (other.site != null)
				return false;
		} else if (!site.equals(other.site))
			return false;
		return true;
	}

	/**
	 * @brief Transforms a price tag string into a PricePoint.
	 * @param price The string representation of a price tag.
	 * @param locale The locale considered for extracting the price tag.
	 * @param retrievedTime
	 * @return
	 * @throws ParseException
	 */
	public static PricePoint valueOf(final String price, final Locale locale, LocalDate retrievedTime) throws ParseException {
		final NumberFormat noFormat = NumberFormat.getNumberInstance(locale);
		if (noFormat instanceof DecimalFormat) {
			((DecimalFormat) noFormat).setParseBigDecimal(true);
		}

		BigDecimal nominalValue = (BigDecimal) noFormat.parse(price);
		if (nominalValue.stripTrailingZeros().scale() <= 0 && nominalValue.compareTo(BigDecimal.valueOf(100)) >= 1)
			nominalValue = nominalValue.divide(BigDecimal.valueOf(100));

		return new PricePoint(null, nominalValue, Currency.getInstance(locale), retrievedTime, null);
	}
}
