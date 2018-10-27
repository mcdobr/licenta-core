package me.mircea.licenta.core.infoextraction;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;

import me.mircea.licenta.core.entities.Book;
import me.mircea.licenta.core.entities.WebWrapper;
import me.mircea.licenta.core.utils.HtmlUtil;

public class WrapperStrategyTest {
	@Test
	public void shouldExtractProduct() throws IOException {
		Element mainContent = HtmlUtil.extractMainContent(Jsoup.connect("https://carturesti.ro/carte/inima-omului-171704655?p=3").get());
		WrapperGenerationStrategy donor = new HeuristicalStrategy();
		
		WebWrapper wrapper = donor.getWrapper(mainContent);
		InformationExtractionStrategy strategy = new WrapperStrategy(wrapper);
		
		//TODO: fix your damn interface
		Document dummyDoc = Jsoup.parseBodyFragment(mainContent.outerHtml());
		
		Book extractedBook = strategy.extractBook(null, dummyDoc);
		assertEquals("Inima omului", extractedBook.getTitle());
		
		List<String> expectedAuthors = Arrays.asList("Jon Kalman Stefansson");
		assertEquals(expectedAuthors, extractedBook.getAuthors());
	}
}
