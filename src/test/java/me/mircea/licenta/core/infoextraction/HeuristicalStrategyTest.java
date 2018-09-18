package me.mircea.licenta.core.infoextraction;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Test;

public class HeuristicalStrategyTest {

	@Test
	public void shouldExtractElementsFromDownloadedPage() throws IOException {
		final ClassLoader classLoader = getClass().getClassLoader();
		final URL resource = classLoader.getResource("extractionTestInput.html");
		assertNotNull(resource);

		File inputFile = new File(resource.getFile());
		assertTrue(inputFile.exists());

		Document doc = Jsoup.parse(inputFile, "UTF-8", "http://www.librariilealexandria.ro/carte");
		InformationExtractionStrategy extractionStrategy = new HeuristicalStrategy();
		
		Elements productElements = extractionStrategy.getProductElements(doc);
		assertTrue(2000 <= productElements.size());
	}
}
