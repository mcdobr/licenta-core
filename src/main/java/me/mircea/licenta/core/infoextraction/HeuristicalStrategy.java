package me.mircea.licenta.core.infoextraction;

import java.net.MalformedURLException;
import java.text.ParseException;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
	public Book extractBook(Element bookCard, Document bookPage) {
		Preconditions.checkNotNull(bookCard);

		Book book = new Book();
		book.setTitle(extractTitle(bookCard));
		book.setImageUrl(extractImageUrl(bookPage));
		book.setDescription(extractDescription(bookPage));
		
		final Map<String, String> bookAttributes = extractAttributes(bookPage);
		if (bookAttributes.isEmpty())
			logger.debug("AttributesMap is empty on {}", bookPage.location());
		
		book.setAuthors(extractAuthors(bookPage));
		book.setIsbn(extractIsbn(bookPage));
		book.setFormat(extractFormat(bookPage));
		book.setPublisher(extractPublisher(bookPage));
		
		book.getKeywords().addAll(splitKeywords(book.getTitle(), book.getIsbn(), book.getPublisher(), book.getFormat()));
		book.getKeywords().addAll(splitKeywords(book.getAuthors()));
		return book;
	}
	
	@Override
	public String extractTitle(Element htmlElement) {
		return htmlElement.select(CssUtil.makeClassOrIdContains(TextContentAnalyzer.titleWordSet)).first().text();
	}
	
	@Override
	public String extractAuthors(Element htmlElement) {
		Map<String, String> attributes = this.extractAttributes(htmlElement);
		Element authorElement = htmlElement.select(CssUtil.makeClassOrIdContains(TextContentAnalyzer.authorWordSet)).first();
		String text = null;
		if (authorElement != null) {
			text = authorElement.text();
		} else {	
			Optional<String> authorAttribute = attributes.keySet().stream()
				.filter(TextContentAnalyzer.authorWordSet::contains)
				.findFirst();
			if (authorAttribute.isPresent())
				text = attributes.get(authorAttribute.get());
		}
		
		return text;
	}
	
	@Override
	public String extractImageUrl(Element htmlElement) {
		Elements imagesWithAlt = htmlElement.select("img[alt]");
		imagesWithAlt.removeAll(htmlElement.select("img[alt='']"));
		
		String result = imagesWithAlt.first().absUrl("src");
		if (result.isEmpty()) {
			Elements imagesInMetadata = htmlElement.select("meta[property*='image']");
			result = imagesInMetadata.first().attr("content");
		}
		
		return result;
	}
	
	@Override
	public String extractIsbn(Element htmlElement) {
		Map<String, String> attributes = this.extractAttributes(htmlElement);
		String isbn = null;
		Optional<String> isbnAttribute = attributes.keySet().stream().filter(TextContentAnalyzer.codeWordSet::contains).findFirst();
		if (isbnAttribute.isPresent())
			isbn = attributes.get(isbnAttribute.get()).replaceAll("^[ a-zA-Z]*", "");
		
		return isbn;
	}
	
	@Override
	public String extractFormat(Element htmlElement) {
		Map<String, String> attributes = this.extractAttributes(htmlElement);
		String format = null;
		Optional<String> formatAttribute = attributes.values().stream().filter(TextContentAnalyzer.formatsWordSet::containsKey).findFirst();
		if (formatAttribute.isPresent())
			format = attributes.get(formatAttribute.get());
		
		return format;
	}
	
	@Override
	public String extractPublisher(Element htmlElement) {
		Map<String, String> attributes = this.extractAttributes(htmlElement);
		String publisher = null;
		Optional<String> publisherAttribute = attributes.keySet().stream().filter(TextContentAnalyzer.publisherWordSet::contains).findFirst();
		if (publisherAttribute.isPresent())
			publisher = attributes.get(publisherAttribute.get());
		
		return publisher;
	}
	
	@Override
	public PricePoint extractPricePoint(Element htmlElement, Locale locale, Instant retrievedTime) {
		Preconditions.checkNotNull(htmlElement);
		String price = htmlElement.select(CssUtil.makeClassOrIdContains(TextContentAnalyzer.priceWordSet)).text();

		PricePoint pricePoint = null;
		try {
			final String url = HtmlUtil.extractFirstLinkOfElement(htmlElement);
			pricePoint = PricePoint.valueOf(price, locale, retrievedTime, url);
		} catch (ParseException e) {
			logger.warn("Price tag was ill-formated {}", price);
		} catch (MalformedURLException e) {
			logger.warn("Url was malformed {}", e);
		}

		return pricePoint;
	}

	@Override
	public String extractDescription(Document bookPage) {
		Preconditions.checkNotNull(bookPage);
		Element descriptionElement = bookPage.select("[class*='descri']").first();
		return (descriptionElement != null) ? descriptionElement.text() : null;
	}

	@Override
	public Map<String, String> extractAttributes(Element bookPage) {
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
		//TODO: refactor this
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
		//TODO: handle currency
		String priceRegexSelector = ":matchesOwn(lei)";
		Element priceRegexElement = bookPage.select(priceRegexSelector).first();
		if (!priceRegexElement.equals(priceElement)) {
			String priceHtml = priceElement.html();
			String priceRegexHtml = priceRegexElement.html();

			String str = bookPage.outerHtml();
			
			int indexOfPrice = str.indexOf(priceHtml);
			int indexOfRegex = str.indexOf(priceRegexHtml);
			
			//TODO: this may break
			priceElement = (indexOfPrice <= indexOfRegex) ? priceElement : priceRegexElement;
		}
		
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
		wrapper.setImageLinkSelector("img[alt]:not(img[alt='']),meta[property*='image']");

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
		//TODO: refactor this
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
				// Gets mode of tag name
				final Map<String, Long> tagNameFrequencies = elements.stream().map(Element::tagName)
						.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
				final long maxTagFrequency = tagNameFrequencies.values().stream().max(Long::compare).orElse(0L);
				final List<String> tagModes = tagNameFrequencies.entrySet().stream()
						.filter(tuple -> tuple.getValue() == maxTagFrequency).map(Map.Entry::getKey)
						.collect(Collectors.toList());
				String tag = tagModes.get(0);

				logger.info("Tagname frequencies: {}", tagNameFrequencies);

				// TODO: handle case when not all are the same, handle case when tag is not unique to the site
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
	
	private Set<String> splitKeywords(String ...values)
	{
		final int MIN_KEYWORD_LENGTH = 3;
		Set<String> result = new HashSet<>();
		for (String str : values) {
			if (str != null) {
				List<String> eligibleWords = Arrays.asList(str.split(" "))
						.stream()
						.filter(word -> word.length() >= MIN_KEYWORD_LENGTH)
						.map(word -> word.toLowerCase())
						.collect(Collectors.toList());
				result.addAll(eligibleWords);
			}
		}
		return result;
	}
}
