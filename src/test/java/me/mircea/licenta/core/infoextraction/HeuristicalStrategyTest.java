package me.mircea.licenta.core.infoextraction;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import me.mircea.licenta.core.entities.Book;
import me.mircea.licenta.core.entities.WebWrapper;
import me.mircea.licenta.core.utils.HtmlUtil;

public class HeuristicalStrategyTest {
	InformationExtractionStrategy extractionStrategy = new HeuristicalStrategy();
	final ClassLoader classLoader = getClass().getClassLoader();
	
	@Test
	public void shouldExtractElementsFromDownloadedMultiPage() throws IOException {
		final URL resource = classLoader.getResource("heuristicMock.html");
		assertNotNull(resource);

		File inputFile = new File(resource.getFile());
		assertTrue(inputFile.exists());

		Document doc = Jsoup.parse(inputFile, "UTF-8", "http://www.librariilealexandria.ro/carte");

		Elements productElements = extractionStrategy.extractBookCards(doc);
		assertNotNull(productElements);
		assertTrue(2000 <= productElements.size());
	}

	@Test
	public void shouldExtractAttributes() throws IOException {
		Document doc = HtmlUtil.sanitizeHtml(
				Jsoup.connect("https://carturesti.ro/carte/trecute-vieti-de-doamne-si-domnite-82699986?p=1995").get());

		final URL resource = classLoader.getResource("heuristicFragmentMock.html");
		File inputFile = new File(resource.getFile());
		Element htmlElement = Jsoup.parse(inputFile, "UTF-8");

		Book book = extractionStrategy.extractBook(htmlElement, doc);
		assertNotNull(book.getIsbn());
		assertNotEquals(book.getIsbn().trim(), "");
	}

	@Test
	public void shouldCreateAppropriateWrapperOnCarturesti() throws IOException {
		String url = "https://carturesti.ro/carte/pedaland-prin-viata-181658144?p=2";
		Element mainContent = HtmlUtil.extractMainContent(Jsoup.connect(url).get());

		final URL resource = classLoader.getResource("heuristicGridMock.html");
		File inputFile = new File(resource.getFile());
		
		Elements additionals = new Elements();
		additionals.add(Jsoup.parse(inputFile, "UTF-8"));

		WrapperGenerationStrategy strategy = new HeuristicalStrategy();
		WebWrapper wrapper = strategy.generateWrapper(mainContent, additionals);

		System.out.println("Carturesti: " + wrapper.toString());

		assertEquals(".titluProdus", wrapper.getTitleSelector());
		assertEquals(".autorProdus", wrapper.getAuthorsSelector());
		assertEquals(".pret", wrapper.getPriceSelector());
		assertEquals(".productAttr", wrapper.getAttributeSelector());
		assertEquals(".product-grid-container", wrapper.getBookCardSelector());
	}

	@Test
	public void shouldCreateAppropriateWrapperOnLibris() throws IOException {
		String url = "https://www.libris.ro/naufragii-akira-yoshimura-HUM978-606-779-038-2--p1033264.html";
		Element mainContent = HtmlUtil.extractMainContent(Jsoup.connect(url).get());
		WrapperGenerationStrategy strategy = new HeuristicalStrategy();
		WebWrapper wrapper = strategy.generateWrapper(mainContent);

		System.out.println("Libris: " + wrapper.toString());
		assertEquals("#text_container>p", wrapper.getAttributeSelector());
	}

	@Test
	public void shouldCreateAppropriateWrapperOnAlexandria() throws IOException {
		String url = "http://www.librariilealexandria.ro/elita-din-umbra";
		Element mainContent = HtmlUtil.extractMainContent(Jsoup.connect(url).get());
		WrapperGenerationStrategy strategy = new HeuristicalStrategy();
		WebWrapper wrapper = strategy.generateWrapper(mainContent);

		System.out.println("Alexandria: " + wrapper.toString());

		assertEquals(".product-author", wrapper.getAuthorsSelector());
		assertEquals(".big-text>b", wrapper.getPriceSelector());
		// TODO: add more (exclude .price)
	}
}
