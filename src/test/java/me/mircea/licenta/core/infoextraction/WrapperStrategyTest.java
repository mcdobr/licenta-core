package me.mircea.licenta.core.infoextraction;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import me.mircea.licenta.core.entities.Book;
import me.mircea.licenta.core.entities.PricePoint;
import me.mircea.licenta.core.entities.WebWrapper;
import me.mircea.licenta.core.utils.HtmlUtil;

public class WrapperStrategyTest {

	@Test
	public void shouldGetBookCards() throws IOException {
		Element singlePageContent = HtmlUtil.extractMainContent(Jsoup.connect(
				"https://www.libris.ro/medicina-nutritie-si-buna-dispozitie-simona-HUM978-973-50-5918-7--p1250968.html")
				.get());

		Element multiPageContent = HtmlUtil.sanitizeHtml(Jsoup.connect("https://www.libris.ro/carti").get());

		RuleBasedStrategy donor = new HeuristicalStrategy();
		WebWrapper wrapper = donor.generateWrapper(singlePageContent,
				new Elements(Jsoup.parseBodyFragment(multiPageContent.outerHtml())));
		InformationExtractionStrategy strategy = new WrapperStrategy(wrapper);

		// TODO: fix your damn interface
		Document dummyDoc = Jsoup.parseBodyFragment(multiPageContent.outerHtml());

		Elements bookCards = dummyDoc.select(wrapper.getBookCardSelector());
		assertTrue(40 <= bookCards.size());
		System.out.println(wrapper.toString());
	}

	@Test
	public void shouldExtractProduct() throws IOException {
		Element mainContent = HtmlUtil
				.extractMainContent(Jsoup.connect("https://carturesti.ro/carte/inima-omului-171704655?p=3").get());
		WrapperGenerationStrategy donor = new HeuristicalStrategy();

		WebWrapper wrapper = donor.generateWrapper(mainContent);
		InformationExtractionStrategy strategy = new WrapperStrategy(wrapper);

		// TODO: fix your damn interface
		Document dummyDoc = Jsoup.parseBodyFragment(mainContent.outerHtml());

		Book extractedBook = strategy.extractBook(null, dummyDoc);
		assertEquals("Inima omului", extractedBook.getTitle());

		Map<String, String> attributes = strategy.extractBookAttributes(dummyDoc);
		assertFalse(attributes.isEmpty());

		List<String> expectedAuthors = Arrays.asList("Jon Kalman Stefansson");
		assertEquals(expectedAuthors, extractedBook.getAuthors());

		assertFalse(extractedBook.getDescription().isEmpty());
		System.out.println(extractedBook.toString());
	}

	@Test
	public void shouldExtractPrice() throws IOException {
		Element mainContent = HtmlUtil
				.extractMainContent(Jsoup.connect("https://carturesti.ro/carte/inima-omului-171704655?p=3").get());
		WrapperGenerationStrategy donor = new HeuristicalStrategy();

		WebWrapper wrapper = donor.generateWrapper(mainContent);
		InformationExtractionStrategy strategy = new WrapperStrategy(wrapper);

		PricePoint price = strategy.extractPricePoint(mainContent, Locale.forLanguageTag("ro-ro"), LocalDate.now(),
				null);
		assertEquals(35.96, price.getNominalValue().doubleValue(), 1e-5);

		// TODO: make it so that it extracts off of alexandria
	}
}
