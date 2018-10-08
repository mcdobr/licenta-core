package me.mircea.licenta.core.infoextraction;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import me.mircea.licenta.core.entities.PricePoint;
import me.mircea.licenta.core.entities.Book;
import me.mircea.licenta.core.entities.Site;

public class HeuristicalStrategy implements InformationExtractionStrategy {
	private static final Logger logger = LoggerFactory.getLogger(HeuristicalStrategy.class);
	private static final Pattern isbnPattern = Pattern.compile("(?=[-\\d\\ xX]{10,})\\d+[-\\ ]?\\d+[-\\ ]?\\d+[-\\ ]?\\d*[-\\ ]?[\\dxX]");
	private static final String IMAGE_WITH_LINK_SELECTOR = "[class*='produ']:has(img):has(a)";
	private static final String PRODUCT_SELECTOR = String.format("%s:not(:has(%s))", IMAGE_WITH_LINK_SELECTOR,
			IMAGE_WITH_LINK_SELECTOR);

	private static final Set<String> authorWordSet = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
	private static final Set<String> codeWordSet = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
	private static final Map<String, String> formatsWordSet = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
	private static final Set<String> yearWordSet = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
	private static final Set<String> publisherWordSet = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
	
	static {
		authorWordSet.add("autor");
		authorWordSet.add("autori");
		authorWordSet.add("author");
		authorWordSet.add("authors");

		codeWordSet.add("isbn");
		codeWordSet.add("cod");
		
		formatsWordSet.put("hardcover", "hardcover");
		formatsWordSet.put("paperback", "paperback");
		formatsWordSet.put("pdf", "pdf");
		formatsWordSet.put("epub", "epub");
		formatsWordSet.put("mobi", "mobi");
	
		formatsWordSet.put("cartonata", "hardcover");
		formatsWordSet.put("necartonata", "paperback");
		
		yearWordSet.add("an");
		yearWordSet.add("anul");
		yearWordSet.add("year");
		
		publisherWordSet.add("publisher");
		publisherWordSet.add("editura");
	}
	
	@Override
	public Elements extractProductHtmlElements(Document doc) {
		Preconditions.checkNotNull(doc);
		return doc.select(PRODUCT_SELECTOR);
	}

	@Override
	public Book extractBook(Element htmlElement, Document productPage) {
		Preconditions.checkNotNull(htmlElement);

		String title = htmlElement.select("[class*='titl'],[class*='nume'],[class*='name']").first().text();

		Book book = new Book();
		String imageUrl = htmlElement.select("img[src]").first().absUrl("src");
		book.setCoverUrl(imageUrl);
		
		
		// Extract product attributes and attach to product
		final Map<String, String> productAttributes = this.extractProductAttributes(productPage);
		if (productAttributes.isEmpty())
			logger.debug("AttributesMap is empty on {}", productPage.location());

		book.setTitle(title);
		
		productAttributes.keySet().stream().filter(authorWordSet::contains)
			.findFirst().ifPresent(authorTag -> 
			book.setAuthors(Arrays.asList(productAttributes.get(authorTag).split("[.,&]"))));
		
		productAttributes.keySet().stream().filter(codeWordSet::contains).findFirst()
			.ifPresent(key -> book.setIsbn(productAttributes.get(key).replaceAll("^[ a-zA-Z]*", "")));
		
		productAttributes.values().stream().filter(formatsWordSet::containsKey)
			.findFirst().ifPresent(format -> book.setFormat(formatsWordSet.get(format)));
		
		productAttributes.keySet().stream().filter(yearWordSet::contains)
			.findFirst().ifPresent(key -> book.setReleaseYear(Integer.parseInt(productAttributes.get(key))));
		
		productAttributes.keySet().stream().filter(publisherWordSet::contains)
			.findFirst().ifPresent(key -> book.setPublishingHouse(productAttributes.get(key)));
		
		book.setDescription(this.extractProductDescription(productPage));

		return book;
	}

	@Override
	public PricePoint extractPricePoint(Element htmlElement, Locale locale, LocalDate retrievedDay, Site site) {
		Preconditions.checkNotNull(htmlElement);
		String price = htmlElement.select("[class*='pret'],[class*='price']").text();

		PricePoint pricePoint = null;
		try {
			pricePoint = PricePoint.valueOf(price, locale, retrievedDay, site);
		} catch (ParseException e) {
			logger.warn("Price tag was ill-formated {}", price);
		}

		return pricePoint;
	}

	@Override
	public String extractProductDescription(Document productPage) {
		Preconditions.checkNotNull(productPage);
		Element descriptionElement = productPage.select("[class*='descri']").first();
		return (descriptionElement != null) ? descriptionElement.text() : null;
	}

	@Override
	public boolean hasProducts(Document page) {
		return !extractProductHtmlElements(page).isEmpty();
	}

	/**
	 * @brief Function that extracts the attribute of a product off of its page.
	 * @param productPage
	 * @return An associative array of attributes.
	 * @throws NullPointerException
	 *             if the passed document is null.
	 */

	private Map<String, String> extractProductAttributes(Document productPage) {
		Preconditions.checkNotNull(productPage);
		Map<String, String> productAttributes = new TreeMap<>();

		Matcher isbnMatcher = isbnPattern.matcher(productPage.text());
		if (isbnMatcher.find()) {
			String isbn = isbnMatcher.group();
			logger.debug("Found isbn {}", isbn);

			String findIsbnElement = String.format("*:contains(%s)", isbn);
			Element isbnElement = productPage.select(findIsbnElement).last();

			if (isbnElement.text().trim().equals(isbn))
				isbnElement = isbnElement.parent();

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
}
