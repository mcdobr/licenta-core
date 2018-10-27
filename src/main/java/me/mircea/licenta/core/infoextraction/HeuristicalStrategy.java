package me.mircea.licenta.core.infoextraction;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
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
import me.mircea.licenta.core.utils.TextContentAnalyzer;

public class HeuristicalStrategy implements RuleBasedStrategy {
	private static final Logger logger = LoggerFactory.getLogger(HeuristicalStrategy.class);
	private static final Pattern isbnPattern = Pattern.compile("(?=[-\\d\\ xX]{10,})\\d+[-\\ ]?\\d+[-\\ ]?\\d+[-\\ ]?\\d*[-\\ ]?[\\dxX]");
	private static final String IMAGE_WITH_LINK_SELECTOR = "[class*='produ']:has(img):has(a)";
	private static final String PRODUCT_CARD_SELECTOR = CssUtil.makeLeafOfSelector(IMAGE_WITH_LINK_SELECTOR);
	
	@Override
	public Elements extractBookCards(Document doc) {
		Preconditions.checkNotNull(doc);
		return doc.select(PRODUCT_CARD_SELECTOR);
	}

	@Override
	public Book extractBook(Element htmlElement, Document bookPage) {
		Preconditions.checkNotNull(htmlElement);

		Book book = new Book();
		String title = htmlElement.select(CssUtil.makeClassOrIdContains(TextContentAnalyzer.titleWordSet)).first().text();
		book.setTitle(title);
		
		String imageUrl = htmlElement.select("img[src]").first().absUrl("src");
		book.setCoverUrl(imageUrl);
		
		// Extract book specs and attach to book
		final Map<String, String> bookAttributes = this.extractBookAttributes(bookPage);
		if (bookAttributes.isEmpty())
			logger.debug("AttributesMap is empty on {}", bookPage.location());

		
		Element authorElement = bookPage.select(CssUtil.makeClassOrIdContains(TextContentAnalyzer.authorWordSet)).first();
		if (authorElement != null) {
			book.setAuthors(Arrays.asList(authorElement.text().split(TextContentAnalyzer.authorSeparatorsRegex)));
		} else {
			bookAttributes.keySet().stream()
				.filter(TextContentAnalyzer.authorWordSet::contains)
				.findFirst().ifPresent(authorTag -> book.setAuthors(Arrays.asList(bookAttributes.get(authorTag).split(TextContentAnalyzer.authorSeparatorsRegex))));
		}
		
		bookAttributes.keySet().stream().filter(TextContentAnalyzer.codeWordSet::contains).findFirst()
			.ifPresent(key -> book.setIsbn(bookAttributes.get(key).replaceAll("^[ a-zA-Z]*", "")));
		
		bookAttributes.values().stream().filter(TextContentAnalyzer.formatsWordSet::containsKey)
			.findFirst().ifPresent(format -> book.setFormat(TextContentAnalyzer.formatsWordSet.get(format)));
		
		bookAttributes.keySet().stream().filter(TextContentAnalyzer.yearWordSet::contains)
			.findFirst().ifPresent(key -> book.setReleaseYear(Integer.parseInt(bookAttributes.get(key))));
		
		bookAttributes.keySet().stream().filter(TextContentAnalyzer.publisherWordSet::contains)
			.findFirst().ifPresent(key -> book.setPublishingHouse(bookAttributes.get(key)));
		
		book.setDescription(this.extractBookDescription(bookPage));

		return book;
	}

	@Override
	public PricePoint extractPricePoint(Element htmlElement, Locale locale, LocalDate retrievedDay, Site site) {
		Preconditions.checkNotNull(htmlElement);
		String price = htmlElement.select(CssUtil.makeClassOrIdContains(TextContentAnalyzer.priceWordSet)).text();

		PricePoint pricePoint = null;
		try {
			pricePoint = PricePoint.valueOf(price, locale, retrievedDay,
					HtmlUtil.extractFirstLinkOfElement(htmlElement), site);
		} catch (ParseException e) {
			logger.warn("Price tag was ill-formated {}", price);
		}

		return pricePoint;
	}

	@Override
	public String extractBookDescription(Document bookPage) {
		Preconditions.checkNotNull(bookPage);
		Element descriptionElement = bookPage.select("[class*='descri']").first();
		return (descriptionElement != null) ? descriptionElement.text() : null;
	}

	/**
	 * @brief Function that extracts the attribute of a book off of its page.
	 * @param bookPage
	 * @return An associative array of attributes.
	 * @throws NullPointerException
	 *             if the passed document is null.
	 */

	@Override
	public Map<String, String> extractBookAttributes(Document bookPage) {
		Preconditions.checkNotNull(bookPage);
		Map<String, String> bookAttributes = new TreeMap<>();

		Matcher isbnMatcher = isbnPattern.matcher(bookPage.text());
		if (isbnMatcher.find()) {
			String isbn = isbnMatcher.group();
			logger.debug("Found isbn {}", isbn);

			String findIsbnElement = String.format("*:contains(%s)", isbn);
			Element isbnElement = bookPage.select(findIsbnElement).last();

			if (isbnElement.text().trim().equals(isbn))
				isbnElement = isbnElement.parent();

			// TODO: tag ending aware?
			Elements keyValuePairs = isbnElement.siblingElements();
			keyValuePairs.add(isbnElement);
			for (Element element : keyValuePairs) {
				String[] keyValuePair = element.text().split(":", 2);

				if (keyValuePair.length > 1)
					bookAttributes.put(keyValuePair[0].trim(), keyValuePair[1].trim());
			}
		}

		return bookAttributes;
	}

	@Override
	public WebWrapper generateWrapper(Element bookPage, Elements additionals) {
		WebWrapper wrapper = new WebWrapper();

		String titleSelector = CssUtil.makeClassOrIdContains(TextContentAnalyzer.titleWordSet);
		Element titleElement = bookPage.select(titleSelector).select(String.format(":not(:has(%s))", titleSelector))
				.first();
		if (titleElement != null)
			wrapper.setTitleSelector(generateCssSelectorFor(new Elements(titleElement)));

		String authorsSelector = CssUtil.makeClassOrIdContains(TextContentAnalyzer.authorWordSet);
		Element authorsElement = bookPage.select(authorsSelector).first();
		if (authorsElement != null)
			wrapper.setAuthorsSelector(generateCssSelectorFor(new Elements(authorsElement)));

		String priceSelector = CssUtil.makeClassOrIdContains(TextContentAnalyzer.priceWordSet);
		Element priceElement = bookPage.select(priceSelector).first();
		if (priceElement != null)
			wrapper.setPriceSelector(generateCssSelectorFor(new Elements(priceElement)));

		String isbnSelector = ":matchesOwn((?=[-\\d\\ xX]{10,})\\d+[-\\ ]?\\d+[-\\ ]?\\d+[-\\ ]?\\d*[-\\ ]?[\\dxX])";
		Element isbnElement = bookPage.select(isbnSelector).first();
		Element parent = isbnElement.parent();

		// TODO: refine this: If the book code element has no class then those are
		// probably the whole thing
		Elements attributeElements = null;
		if (isbnElement.className().isEmpty()) {
			attributeElements = isbnElement.siblingElements();
			attributeElements.add(isbnElement);
		} else {
			attributeElements = parent.siblingElements();
			attributeElements.add(parent);
		}

		wrapper.setAttributeSelector(generateCssSelectorFor(attributeElements));
		wrapper.setImageLinkSelector("img[alt]");

		String descriptionSelector = CssUtil.makeClassOrIdContains(TextContentAnalyzer.descriptionWordSet);
		Element descriptionElement = bookPage.select(descriptionSelector).first();
		if (descriptionElement != null)
			wrapper.setDescriptionSelector(generateCssSelectorFor(new Elements(descriptionElement)));

		if (!additionals.isEmpty()) {
			Elements bookCards = new Elements();
			for (Element element : additionals) {
				bookCards.addAll(element.select(PRODUCT_CARD_SELECTOR));
			}
			wrapper.setBookCardSelector(generateCssSelectorFor(bookCards));
		}

		return wrapper;
	}
	
	
	@Override
	public String generateCssSelectorFor(Elements elements) {
		Preconditions.checkArgument(!elements.isEmpty());

		String selector = null;
		if (elements.size() == 1) {
			Element elem = elements.first();

			if (!elem.id().isEmpty())
				selector = "#" + elem.id();
			else if (!elem.className().isEmpty())
				selector = "." + String.join(".", elem.classNames());
			else
				selector = generateCssSelectorFor(new Elements(elem.parent())) + ">" + elem.tagName();
		} else {
			// Get the mode of class name
			final Map<String, Long> classNameFrequencies = elements.stream().map(Element::className)
					.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
			final long maxFrequency = classNameFrequencies.values().stream().max(Long::compare).orElse(0L);
			final List<String> classModes = classNameFrequencies.entrySet().stream()
					.filter(tuple -> tuple.getValue() == maxFrequency).map(Map.Entry::getKey)
					.collect(Collectors.toList());

			if (!classModes.get(0).isEmpty()) {
				selector = "." + classModes.get(0);
			} else {
				// TODO: get mode of tag name
				final Map<String, Long> tagNameFrequencies = elements.stream().map(Element::tagName)
						.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
				final long maxTagFrequency = tagNameFrequencies.values().stream().max(Long::compare).orElse(0L);
				final List<String> tagModes = tagNameFrequencies.entrySet().stream()
						.filter(tuple -> tuple.getValue() == maxTagFrequency).map(Map.Entry::getKey)
						.collect(Collectors.toList());
				String tag = tagModes.get(0);

				System.out.println(tagNameFrequencies);

				// TODO: handle case when not all are the same
				// TODO: handle case when tag is not unique to the site
				// Also this is probably breakable
				Element parent = elements.first().parent();
				if (!parent.id().isEmpty())
					selector = generateCssSelectorFor(new Elements(parent)) + ">" + tag;
				else
					selector = generateCssSelectorFor(new Elements(parent));
			}
		}
		
		return selector;
	}
}
