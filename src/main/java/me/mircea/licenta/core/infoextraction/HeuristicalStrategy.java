package me.mircea.licenta.core.infoextraction;

import java.net.MalformedURLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import me.mircea.licenta.core.entities.PricePoint;
import me.mircea.licenta.core.entities.Product;
import me.mircea.licenta.core.entities.Site;
import me.mircea.licenta.core.productsdb.HibernateUtil;
import me.mircea.licenta.core.utils.ImmutablePair;

public class HeuristicalStrategy implements InformationExtractionStrategy {
	private static final Logger logger = LoggerFactory.getLogger(HeuristicalStrategy.class);
	private static final Pattern isbnPattern = Pattern.compile("[0-9]+[- ][0-9]+[- ][0-9]+[- ][0-9]*[- ]*[xX0-9]");
	private static final String imageWithLinkSelector = "[class*='produ']:has(img):has(a)";
	private static final String PRODUCT_SELECTOR = String.format("%s:not(:has(%s))", imageWithLinkSelector,
			imageWithLinkSelector);

	@Override
	public Elements getProductElements(Document doc) {
		Preconditions.checkNotNull(doc);
		return doc.select(PRODUCT_SELECTOR);
	}

	//TODO: split up into 2
	@Override
	public ImmutablePair<Product, PricePoint> extractProductAndPricePoint(Element htmlElement, Locale locale,
			LocalDate retrievedDay, Site site) {
		Preconditions.checkNotNull(htmlElement);
		String title = htmlElement.select("[class*='titl'],[class*='nume'],[class*='name']").text();
		String price = htmlElement.select("[class*='pret'],[class*='price']").text();
		logger.debug("{} priced at {}", title, price);

		Product product = new Product();
		product.setTitle(title);

		PricePoint pricePoint = null;
		try {
			pricePoint = PricePoint.valueOf(price, locale, retrievedDay, site);
		} catch (ParseException e) {
			logger.warn("Price tag was ill-formated {}", price);
		}

		return new ImmutablePair<>(product, pricePoint);
	}

	/**
	 * @brief This method extracts a set of key-value pairs that shortly describe
	 *        the product.
	 * 
	 */
	@Override
	public Map<String, String> extractProductAttributes(Document productPage) {
		Preconditions.checkNotNull(productPage);
		Map<String, String> productAttributes = new TreeMap<>();

		Matcher isbnMatcher = isbnPattern.matcher(productPage.text());
		if (isbnMatcher.find()) {
			String isbn = isbnMatcher.group();
			logger.info("Found isbn {}", isbn);

			String findIsbnElement = String.format("*:contains(%s)", isbn);
			Element isbnElement = productPage.select(findIsbnElement).last();

			if (isbnElement.text().trim().equals(isbn))
				isbnElement = isbnElement.parent();

			// logger.warn(isbnElement.text());

			Elements keyValuePairs = isbnElement.siblingElements();
			keyValuePairs.add(isbnElement);
			for (Element element : keyValuePairs) {
				String[] keyValuePair = element.text().split(":", 2);

				if (keyValuePair.length > 1)
					productAttributes.put(keyValuePair[0].trim(), keyValuePair[1].trim());
			}
		}

		return productAttributes;
	}

	@Override
	public String extractProductDescription(Document productPage) {
		Preconditions.checkNotNull(productPage);
		Element descriptionElement = productPage.select("[class*='descri']").first();
		return (descriptionElement != null) ? descriptionElement.text() : null;
	}

	@Override
	public boolean hasProducts(Document page) {
		return !getProductElements(page).isEmpty();
	}
}
