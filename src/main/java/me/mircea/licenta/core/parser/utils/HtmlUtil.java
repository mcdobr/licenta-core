package me.mircea.licenta.core.parser.utils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.net.InternetDomainName;

public class HtmlUtil {
	private static final Logger logger = LoggerFactory.getLogger(HtmlUtil.class);
	public static final Set<String> htmlTags = new HashSet<>();
	
	private static final ClassLoader classLoader = HtmlUtil.class.getClassLoader();
	
	
	static {
		try {
			final URL resource = classLoader.getResource("htmlTags.csv");
			File inputFile = new File(resource.getFile());
			
			String text = new String(Files.readAllBytes(Paths.get(inputFile.toURI())), StandardCharsets.UTF_8);
			Arrays.asList(text.split(",")).stream().forEach(htmlTags::add);
		} catch (IOException e) {
			logger.error("Could not read file containing all html compliant tags: {}", e);
		}
	}
	
	private HtmlUtil() {
	}

	public static String extractFirstLinkOfElement(Element htmlElement) {
		return htmlElement.select("a[href]").first().absUrl("href");
	}

	public static String getDomainOfUrl(String url) throws MalformedURLException {
		return InternetDomainName.from(new URL(url).getHost()).topPrivateDomain().toString();
	}
	
	public static Element extractMainContent(Document doc) {
		sanitizeHtml(doc);
		
		//TODO: fix this selector (not applies only on last one)
		return doc.select("[id='content'],[class*='continut'],[class*='page']:not(:has([id='content'],[class*='continut'],[class*='page']))").first();
	}
	
	public static Document sanitizeHtml(Document doc) {
		doc.select("nav,footer,script,noscript,style").remove();
		doc.getElementsByAttribute("style").removeAttr("style");
		doc.getElementsByAttributeValueContaining("class", "carousel").remove();
		doc.getElementsByAttributeValueContaining("class", "promo").remove();
		doc.getElementsByAttributeValueContaining("class", "header").remove();
		doc.getElementsByAttributeValueContaining("class", "nav").remove();
		
		return doc;
	}
	
	public static Element removeUniqueHttpIdentifiers(Element doc) {
		doc.getElementsByAttributeValueContaining("name", "csrf").remove();
		return doc;
	}
	
	public static Optional<String> getCanonicalUrl(Element doc) {
		Element canonicalLink = doc.selectFirst("link[rel='canonical']");
		
		String result = null;
		if (canonicalLink != null) {
			result = canonicalLink.absUrl("href");
		} // TODO: add option for meta tag with canonical link
		
		return Optional.ofNullable(result);
	}
}
