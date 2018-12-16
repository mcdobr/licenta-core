package me.mircea.licenta.core.infoextraction;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.Locale;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.mircea.licenta.core.entities.PricePoint;
import me.mircea.licenta.core.entities.WebWrapper;
import me.mircea.licenta.core.utils.HtmlUtil;

public class WrapperStrategyTest {
	static final Logger logger = LoggerFactory.getLogger(WrapperStrategyTest.class);
	
	static final ClassLoader classLoader = WrapperStrategyTest.class.getClassLoader();
	static final File bookPageAlexandria = new File(classLoader.getResource("bookPageAlexandria.html").getFile());
	static final File bookPageCarturesti = new File(classLoader.getResource("bookPageCarturesti.html").getFile());
	static final File bookPageLibris = new File(classLoader.getResource("bookPageLibris.html").getFile());
	
	static final String alexandriaUrl = "http://www.librariilealexandria.ro/";	
	static final String carturestiUrl = "https://carturesti.ro/";
	static final String librisUrl = "https://www.libris.ro/";
	
	static Element alexandriaContent;;
	static Element carturestiContent;
	static Element librisContent;
	
	static RuleBasedStrategy donor;
	
	@Before
	public void setUp() throws IOException {
		alexandriaContent = HtmlUtil.extractMainContent(Jsoup.parse(bookPageAlexandria, "UTF-8", alexandriaUrl));
		carturestiContent = HtmlUtil.extractMainContent(Jsoup.parse(bookPageCarturesti, "UTF-8", carturestiUrl));
		librisContent = HtmlUtil.extractMainContent(Jsoup.parse(bookPageLibris, "UTF-8", librisUrl));
		
		donor = new HeuristicalStrategy();
	}
	
	@Test
	public void shouldExtractBookCards() throws IOException {
		Element multiPageContent = HtmlUtil.sanitizeHtml(Jsoup.connect("https://www.libris.ro/carti").get());

		WebWrapper wrapper = donor.generateWrapper(librisContent,
				new Elements(Jsoup.parseBodyFragment(multiPageContent.outerHtml())));
		InformationExtractionStrategy strategy = new WrapperStrategy(wrapper);

		// TODO: fix your damn interface
		Document dummyDoc = Jsoup.parseBodyFragment(multiPageContent.outerHtml());

		Elements bookCards = dummyDoc.select(wrapper.getBookCardSelector());
		assertTrue(40 <= bookCards.size());
		logger.info("Wrapper: {}", wrapper.toString());
	}

	@Test
	public void shouldExtractTitles()
	{
		InformationExtractionStrategy strategy;
		
		strategy = new WrapperStrategy(donor.generateWrapper(alexandriaContent));
		assertEquals("Pentru o genealogie a globalizării", strategy.extractTitle(alexandriaContent));
		
		strategy = new WrapperStrategy(donor.generateWrapper(carturestiContent));
		assertEquals("Inima omului", strategy.extractTitle(carturestiContent));
		
		strategy = new WrapperStrategy(donor.generateWrapper(librisContent));
		assertEquals("Medicina, nutritie si buna dispozitie - Simona Tivadar", strategy.extractTitle(librisContent));
	}
	
	@Test
	public void shouldExtractAuthors()
	{
		InformationExtractionStrategy strategy; 
		
		strategy = new WrapperStrategy(donor.generateWrapper(carturestiContent));
		//assertEquals(Arrays.asList("Jon Kalman Stefansson"), strategy.extractAuthors(carturestiContent));
		
		fail();
	}
	
	@Test
	public void shouldExtractDescriptions()
	{
		fail();
	}

	
	/*
	@Test
	public void shouldExtractProduct() throws IOException {
		Element mainContent = HtmlUtil.extractMainContent(Jsoup.parse(bookPageCarturesti, "UTF-8"));
		WrapperGenerationStrategy donor = new HeuristicalStrategy();

		WebWrapper wrapper = donor.generateWrapper(mainContent);
		InformationExtractionStrategy strategy = new WrapperStrategy(wrapper);

		// TODO: fix your damn interface
		Document dummyDoc = Jsoup.parseBodyFragment(mainContent.outerHtml());

		Book extractedBook = strategy.extractBook(null, dummyDoc);
		assertEquals("Inima omului", extractedBook.getTitle());

		Map<String, String> attributes = strategy.extractAttributes(dummyDoc);
		assertFalse(attributes.isEmpty());

		List<String> expectedAuthors = Arrays.asList("Jon Kalman Stefansson");
		assertEquals(expectedAuthors, extractedBook.getAuthors());

		assertFalse(extractedBook.getDescription().isEmpty());
		System.out.println(extractedBook.toString());
	}*/

	@Test
	public void shouldExtractPrices() throws IOException {
		InformationExtractionStrategy strategy;
		PricePoint price;
		
		strategy = new WrapperStrategy(donor.generateWrapper(alexandriaContent));
		price = strategy.extractPricePoint(alexandriaContent, Locale.forLanguageTag("ro-ro"), Instant.now());
		assertEquals(39.00, price.getNominalValue().doubleValue(), 1e-5);
		
		strategy = new WrapperStrategy(donor.generateWrapper(carturestiContent));
		price = strategy.extractPricePoint(carturestiContent, Locale.forLanguageTag("ro-ro"), Instant.now());
		assertEquals(41.95, price.getNominalValue().doubleValue(), 1e-5);
		
		strategy = new WrapperStrategy(donor.generateWrapper(librisContent));
		price = strategy.extractPricePoint(librisContent, Locale.forLanguageTag("ro-ro"), Instant.now());
		assertEquals(32.55, price.getNominalValue().doubleValue(), 1e-5);
	}
}
