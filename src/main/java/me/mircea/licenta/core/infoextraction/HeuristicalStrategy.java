package me.mircea.licenta.core.infoextraction;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import me.mircea.licenta.core.entities.PricePoint;
import me.mircea.licenta.core.entities.Book;
import me.mircea.licenta.core.entities.Site;
import me.mircea.licenta.core.entities.WebWrapper;
import me.mircea.licenta.core.utils.CssUtil;
import me.mircea.licenta.core.utils.HtmlUtil;

public class HeuristicalStrategy implements InformationExtractionStrategy, WrapperGenerationStrategy {
	private static final Logger logger = LoggerFactory.getLogger(HeuristicalStrategy.class);
	private static final Pattern isbnPattern = Pattern.compile("(?=[-\\d\\ xX]{10,})\\d+[-\\ ]?\\d+[-\\ ]?\\d+[-\\ ]?\\d*[-\\ ]?[\\dxX]");
	private static final String IMAGE_WITH_LINK_SELECTOR = "[class*='produ']:has(img):has(a)";
	private static final String PRODUCT_SELECTOR = CssUtil.makeLeafOfSelector(IMAGE_WITH_LINK_SELECTOR);

	public static final Set<String> titleWordSet = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
	public static final Set<String> authorWordSet = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
	public static final Set<String> priceWordSet = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
	public static final Set<String> codeWordSet = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
	public static final Map<String, String> formatsWordSet = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
	public static final Set<String> yearWordSet = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
	public static final Set<String> publisherWordSet = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
	public static final Set<String> descriptionWordSet = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
	
	//TODO: create dictionary class
	static {
		titleWordSet.add("titlu");
		titleWordSet.add("title");
		titleWordSet.add("nume");
		titleWordSet.add("name");
		
		authorWordSet.add("autor");
		authorWordSet.add("autori");
		authorWordSet.add("author");
		authorWordSet.add("authors");

		priceWordSet.add("pret");
		priceWordSet.add("price");
		
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
		
		descriptionWordSet.add("descriere");
		descriptionWordSet.add("description");
	}
	
	@Override
	public Elements extractProductHtmlElements(Document doc) {
		Preconditions.checkNotNull(doc);
		return doc.select(PRODUCT_SELECTOR);
	}

	@Override
	public Book extractBook(Element htmlElement, Document bookPage) {
		Preconditions.checkNotNull(htmlElement);

		Book book = new Book();
		String title = htmlElement.select(CssUtil.makeClassOrIdContains(titleWordSet)).first().text();
		book.setTitle(title);
		
		String imageUrl = htmlElement.select("img[src]").first().absUrl("src");
		book.setCoverUrl(imageUrl);
		
		// Extract product specs and attach to product
		final Map<String, String> productAttributes = this.extractProductAttributes(bookPage);
		if (productAttributes.isEmpty())
			logger.debug("AttributesMap is empty on {}", bookPage.location());

		
		String authorSeparatorsRegex = "[,&]\\s*";
		Element authorElement = bookPage.select(CssUtil.makeClassOrIdContains(authorWordSet)).first();
		if (authorElement != null) {
			book.setAuthors(Arrays.asList(authorElement.text().split(authorSeparatorsRegex)));
		} else {
			productAttributes.keySet().stream()
				.filter(authorWordSet::contains)
				.findFirst().ifPresent(authorTag -> 
				book.setAuthors(Arrays.asList(productAttributes.get(authorTag).split(authorSeparatorsRegex))));
		}
		
		productAttributes.keySet().stream().filter(codeWordSet::contains).findFirst()
			.ifPresent(key -> book.setIsbn(productAttributes.get(key).replaceAll("^[ a-zA-Z]*", "")));
		
		productAttributes.values().stream().filter(formatsWordSet::containsKey)
			.findFirst().ifPresent(format -> book.setFormat(formatsWordSet.get(format)));
		
		productAttributes.keySet().stream().filter(yearWordSet::contains)
			.findFirst().ifPresent(key -> book.setReleaseYear(Integer.parseInt(productAttributes.get(key))));
		
		productAttributes.keySet().stream().filter(publisherWordSet::contains)
			.findFirst().ifPresent(key -> book.setPublishingHouse(productAttributes.get(key)));
		
		book.setDescription(this.extractProductDescription(bookPage));

		return book;
	}

	@Override
	public PricePoint extractPricePoint(Element htmlElement, Locale locale, LocalDate retrievedDay, Site site) {
		Preconditions.checkNotNull(htmlElement);
		String price = htmlElement.select(CssUtil.makeClassOrIdContains(priceWordSet)).text();

		PricePoint pricePoint = null;
		try {
			pricePoint = PricePoint.valueOf(price, locale, retrievedDay, HtmlUtil.extractFirstLinkOfElement(htmlElement), site);
		} catch (ParseException e) {
			logger.warn("Price tag was ill-formated {}", price);
		}

		return pricePoint;
	}

	@Override
	public String extractProductDescription(Document bookPage) {
		Preconditions.checkNotNull(bookPage);
		Element descriptionElement = bookPage.select("[class*='descri']").first();
		return (descriptionElement != null) ? descriptionElement.text() : null;
	}

	@Override
	public boolean hasProducts(Document page) {
		return !extractProductHtmlElements(page).isEmpty();
	}

	/**
	 * @brief Function that extracts the attribute of a product off of its page.
	 * @param bookPage
	 * @return An associative array of attributes.
	 * @throws NullPointerException
	 *             if the passed document is null.
	 */

	private Map<String, String> extractProductAttributes(Document bookPage) {
		Preconditions.checkNotNull(bookPage);
		Map<String, String> productAttributes = new TreeMap<>();

		Matcher isbnMatcher = isbnPattern.matcher(bookPage.text());
		if (isbnMatcher.find()) {
			String isbn = isbnMatcher.group();
			logger.debug("Found isbn {}", isbn);

			String findIsbnElement = String.format("*:contains(%s)", isbn);
			Element isbnElement = bookPage.select(findIsbnElement).last();

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

	@Override
	public WebWrapper getWrapper(Element bookPage) {
		WebWrapper wrapper = new WebWrapper();
		
		String titleSelector = CssUtil.makeClassOrIdContains(titleWordSet);
		Element titleElement = bookPage.select(titleSelector)
				.select(String.format(":not(:has(%s))", titleSelector)).first();
		if (titleElement != null)
			wrapper.setTitleSelector(generateCssSelectorFor(new Elements(titleElement)));
		
		
		String authorsSelector = CssUtil.makeClassOrIdContains(authorWordSet);
		Element authorsElement = bookPage.select(authorsSelector).first();
		if (authorsElement != null)
			wrapper.setAuthorsSelector(generateCssSelectorFor(new Elements(authorsElement)));
		
		String priceSelector = CssUtil.makeClassOrIdContains(priceWordSet);
		Element priceElement = bookPage.select(priceSelector).first();
		if (priceElement != null)
			wrapper.setPriceSelector(generateCssSelectorFor(new Elements(priceElement)));
		
		
		String isbnSelector = ":matchesOwn((?=[-\\d\\ xX]{10,})\\d+[-\\ ]?\\d+[-\\ ]?\\d+[-\\ ]?\\d*[-\\ ]?[\\dxX])";
		Element parent = bookPage.select(isbnSelector).first().parent();
		
		Elements attributeElements = parent.siblingElements();
		attributeElements.add(parent);
		wrapper.setAttributeSelector(generateCssSelectorFor(attributeElements));
		
		wrapper.setImageLinkSelector("img[alt]");
		
		String descriptionSelector = CssUtil.makeClassOrIdContains(descriptionWordSet);
		Element descriptionElement = bookPage.select(descriptionSelector).first();
		if (descriptionElement != null)
			wrapper.setDescriptionSelector(generateCssSelectorFor(new Elements(descriptionElement)));
		
		return wrapper;
	}
	
	
	@Override
	public String generateCssSelectorFor(Elements elements) {
		Preconditions.checkArgument(!elements.isEmpty());
		
		String selector = null;
		if (elements.size() == 1) {
			Element elem = elements.first();
			
			if (elem.hasAttr("id"))
				selector = "#" + elem.id();
			else if (elem.hasAttr("class"))
				selector = "." + elem.className();
		} else {
			// Get the mode of class name
			final Map<String, Long> classNameFrequencies = elements.stream().map(Element::className).
				collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
			
			final long maxFrequency = classNameFrequencies.values().stream().max(Long::compare).orElse(0L);
			
			final List<String> classModes = classNameFrequencies.entrySet().stream()
						.filter(tuple -> tuple.getValue() == maxFrequency)
						.map(Map.Entry::getKey)
						.collect(Collectors.toList());
			
			selector = "." + classModes.get(0);
		}
		
		return selector;
	}
}
