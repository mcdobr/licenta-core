package me.mircea.licenta.core.infoextraction;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import me.mircea.licenta.core.entities.Book;
import me.mircea.licenta.core.utils.HtmlUtil;

public class SemanticWebStrategyTest {
	InformationExtractionStrategy extractionStrategy = new SemanticWebStrategy();
	
	@Test
	public void shouldExtractElementsFromDownloadedMultiPage() throws IOException {
		Document doc = HtmlUtil.sanitizeHtml(Jsoup.connect("https://www.bookdepository.com/category/2630/Romance/browse/viewmode/all").get());
		
		Elements bookElements = extractionStrategy.extractProductHtmlElements(doc);
		assertNotNull(bookElements);
		assertEquals(30, bookElements.size());
	}
	
	@Test
	public void shoudlExtractAttributes() throws IOException {
		Document doc = HtmlUtil.sanitizeHtml(Jsoup.connect("https://www.bookdepository.com/category/2630/Romance/browse/viewmode/all").get());
		Element productElement = extractionStrategy.extractProductHtmlElements(doc).get(0);
		Book book = extractionStrategy.extractBook(productElement);
		
		assertNotNull(book);
		assertNotNull(book.getTitle());
		assertNotNull(book.getIsbn());
	}
}
