package me.mircea.licenta.core.entities;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Currency;
import java.util.Locale;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.google.common.base.Preconditions;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
@javax.persistence.Entity
@Table(name = "pricepoints")
public class PricePoint {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private BigDecimal nominalValue;
	private Currency currency;
	private LocalDate retrievedDay;
	private String url;
	
	@ManyToOne
	private Site site;

	public PricePoint() {
		retrievedDay = LocalDate.now();
	}

	public PricePoint(Long id, BigDecimal nominalValue, Currency currency, LocalDate retrievedDay, String url, Site site) {
		super();
		Preconditions.checkNotNull(retrievedDay);
		
		this.id = id;
		this.nominalValue = nominalValue;
		this.currency = currency;
		this.retrievedDay = retrievedDay;
		this.url = url;
		this.site = site;
	}
	
	public PricePoint(BigDecimal nominalValue, Currency currency, LocalDate retrievedDay, String url, Site site) {
		this(null, nominalValue, currency, retrievedDay, url, site);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
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
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Site getSite() {
		return site;
	}

	public void setSite(Site site) {
		this.site = site;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PricePoint [id=").append(id);
		builder.append(", nominalValue=").append(nominalValue);
		builder.append(", currency=").append(currency);
		builder.append(", retrievedDay=").append(retrievedDay);
		builder.append(", url=").append(url);
		builder.append(", site=").append(site);
		builder.append("]");
		return builder.toString();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((currency == null) ? 0 : currency.hashCode());
		result = prime * result + ((nominalValue == null) ? 0 : nominalValue.hashCode());
		result = prime * result + ((retrievedDay == null) ? 0 : retrievedDay.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		result = prime * result + ((site == null) ? 0 : site.hashCode());
		return result;
	}

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
		
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		
		if (site == null) {
			if (other.site != null)
				return false;
		} else if (!site.equals(other.site))
			return false;
		return true;
	}

	/**
	 * @brief Transforms a price tag string into a PricePoint. If say, a romanian
	 *        site uses the . (dot) decimal separator, this replaces all commas and
	 *        dots to the romanian default decimal separator (which is a comma). If
	 *        the price tag contains no decimal separator whatsoever, the last two
	 *        digits are considered to be cents. If it has more than two digits
	 *        after the normal decimal point, the function considers that a mistake
	 *        on part of the document and makes it right.
	 * @param price
	 *            The string representation of a price tag.
	 * @param locale
	 *            The locale considered for extracting the price tag.
	 * @param retrievedDay
	 *            The day when the price was read.
	 * @param site
	 * @return A pricepoint extracted from the string.
	 * @throws ParseException
	 *             if the String is not formatted according to the locale.
	 */
	public static PricePoint valueOf(String price, final Locale locale, LocalDate retrievedDay, String url, Site site) throws ParseException {
		price = normalizeStringWithLocale(price, locale);
		
		final NumberFormat noFormat = NumberFormat.getNumberInstance(locale);
		if (noFormat instanceof DecimalFormat) {
			((DecimalFormat) noFormat).setParseBigDecimal(true);
		}

		BigDecimal nominalValue = (BigDecimal) noFormat.parse(price);
		if (!price.matches(".*[.,].*") && nominalValue.stripTrailingZeros().scale() <= 0 && nominalValue.compareTo(BigDecimal.valueOf(100)) >= 1)
			nominalValue = nominalValue.divide(BigDecimal.valueOf(100));

		return new PricePoint(null, nominalValue, Currency.getInstance(locale), retrievedDay, url, site);
	}

	/**
	 * Does the actual fixing of the price tag.
	 * @param price
	 * @param locale
	 * @return
	 */
	private static String normalizeStringWithLocale(String price, Locale locale) {
		DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(locale);
		String normalDecimalSeparator = String.valueOf(symbols.getDecimalSeparator());
		String normalGroupingSeparator = String.valueOf(symbols.getGroupingSeparator());
		
		// If a mismatch between locale and website, try and fix it
		final int decimalFirst = price.indexOf(normalDecimalSeparator);
		final int groupingFirst = price.indexOf(normalGroupingSeparator);
		
		final boolean hasNormalDecimalSeparator = (decimalFirst != -1);
		final boolean hasNormalGroupingSeparator = (groupingFirst != -1);
		final boolean hasBothButReversed = hasNormalDecimalSeparator && hasNormalGroupingSeparator && groupingFirst > decimalFirst;
		if (!hasNormalDecimalSeparator) {
			price = price.replaceAll("[.,]", normalDecimalSeparator);
		} else if (!hasNormalGroupingSeparator) { //has decimal but no grouping
			String substring = price.substring(decimalFirst + 1);
			if (substring.matches("^\\d{3,}.*"))
				price = price.replaceAll(normalDecimalSeparator, normalGroupingSeparator);
		} else if (hasBothButReversed) {
			price = swapCharactersInString(price, normalDecimalSeparator.charAt(0), normalGroupingSeparator.charAt(0));
		} 
		
		return price;
	}
	
	private static String swapCharactersInString(final String str, final char first, final char second) {
		char[] chars = str.toCharArray();
		for (int i = 0; i < chars.length; ++i) {
			if (chars[i] == first)
				chars[i] = second;
			else if (chars[i] == second)
				chars[i] = first;
				
		}
		return String.valueOf(chars);
	}
}
