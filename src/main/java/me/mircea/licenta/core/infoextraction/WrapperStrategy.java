package me.mircea.licenta.core.infoextraction;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import me.mircea.licenta.core.entities.Book;
import me.mircea.licenta.core.entities.PricePoint;
import me.mircea.licenta.core.entities.Site;
import me.mircea.licenta.core.entities.WebWrapper;

public class WrapperStrategy implements InformationExtractionStrategy {
	private static final Logger logger = LoggerFactory.getLogger(WrapperStrategy.class);

	private WebWrapper wrapper;

	public WrapperStrategy(WebWrapper wrapper) {
		super();
		this.wrapper = wrapper;
	}

	@Override
	public Elements extractBookCards(Document doc) {
		return doc.select(wrapper.getBookCardSelector());
	}

	@Override
	public Book extractBook(Element htmlElement, Document bookPage) {
		Preconditions.checkNotNull(bookPage);

		Book book = new Book();
		if (wrapper.getTitleSelector() != null)
			book.setTitle(bookPage.selectFirst(wrapper.getTitleSelector()).text());

		if (wrapper.getAttributeSelector() != null) {
			Map<String, String> attributes = extractBookAttributes(bookPage);
			// TODO: handle attributes
		}

		if (wrapper.getAuthorsSelector() != null) {
			List<String> authors = Arrays
					.asList(bookPage.selectFirst(wrapper.getAuthorsSelector()).text().split("[,&]"));
			book.setAuthors(authors);
		}

		if (wrapper.getDescriptionSelector() != null)
			book.setDescription(bookPage.selectFirst(wrapper.getDescriptionSelector()).text());

		return book;
	}

	@Override
	public PricePoint extractPricePoint(Element htmlElement, Locale locale, LocalDate retrievedDay, Site site) {
		String priceText = htmlElement.selectFirst(wrapper.getPriceSelector()).text();

		PricePoint pricePoint = null;
		try {
			pricePoint = PricePoint.valueOf(priceText, locale, retrievedDay, htmlElement.baseUri(), site);
		} catch (ParseException e) {
			logger.warn("Price tag was ill-formated {}", priceText);
		}
		return pricePoint;
	}

	@Override
	public String extractBookDescription(Document bookPage) {
		return bookPage.selectFirst(wrapper.getDescriptionSelector()).text();
	}

	@Override
	public Map<String, String> extractBookAttributes(Document bookPage) {
		Elements specs = bookPage.select(wrapper.getAttributeSelector());
		Map<String, String> attributes = new TreeMap<>();

		for (Element spec : specs) {
			String[] keyValuePair = spec.text().split(":", 2);
			if (keyValuePair.length > 1)
				attributes.put(keyValuePair[0].trim(), keyValuePair[1].trim());
		}

		return attributes;
	}
}
