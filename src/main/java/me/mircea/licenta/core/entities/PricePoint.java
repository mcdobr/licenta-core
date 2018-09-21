package me.mircea.licenta.core.entities;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Currency;
import java.util.Locale;

import javax.persistence.*;

import com.google.common.base.Preconditions;

@Entity
@Table(name = "pricepoints")
public class PricePoint implements Comparable<PricePoint> {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private BigDecimal nominalValue;
	private Currency currency;
	private LocalDate retrievedDay;

	@ManyToOne
	private Site site;

	public PricePoint() {
		retrievedDay = LocalDate.now();
	}

	public PricePoint(Integer id, BigDecimal nominalValue, Currency currency, LocalDate retrievedDay, Site site) {
		super();
		Preconditions.checkNotNull(retrievedDay);
		
		this.id = id;
		this.nominalValue = nominalValue;
		this.currency = currency;
		this.retrievedDay = retrievedDay;
		this.site = site;
	}
	
	public PricePoint(BigDecimal nominalValue, Currency currency, LocalDate retrievedDay, Site site) {
		this(null, nominalValue, currency, retrievedDay, site);
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

	public LocalDate getRetrievedDay() {
		return retrievedDay;
	}

	public void setRetrievedDay(LocalDate retrievedDay) {
		this.retrievedDay = retrievedDay;
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

	/** (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((currency == null) ? 0 : currency.hashCode());
		result = prime * result + ((nominalValue == null) ? 0 : nominalValue.hashCode());
		result = prime * result + ((retrievedDay == null) ? 0 : retrievedDay.hashCode());
		result = prime * result + ((site == null) ? 0 : site.hashCode());
		return result;
	}

	/** (non-Javadoc)
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
	 * @param retrievedDay The day when the price was read.
	 * @param site 
	 * @return A pricepoint extracted from the string.
	 * @throws ParseException if the String is not formatted according to the locale.
	 */
	public static PricePoint valueOf(final String price, final Locale locale, LocalDate retrievedDay, Site site) throws ParseException {
		final NumberFormat noFormat = NumberFormat.getNumberInstance(locale);
		if (noFormat instanceof DecimalFormat) {
			((DecimalFormat) noFormat).setParseBigDecimal(true);
		}

		BigDecimal nominalValue = (BigDecimal) noFormat.parse(price);
		// If the price is like 4700 RON (actually 47.00 RON)
		if (nominalValue.stripTrailingZeros().scale() <= 0 && nominalValue.compareTo(BigDecimal.valueOf(100)) >= 1)
			nominalValue = nominalValue.divide(BigDecimal.valueOf(100));

		return new PricePoint(null, nominalValue, Currency.getInstance(locale), retrievedDay, site);
	}

	/**
	 * @brief Compares pricepoints by day.
	 */
	@Override
	public int compareTo(PricePoint o) {
		return getRetrievedDay().compareTo(o.getRetrievedDay());
	}
}
