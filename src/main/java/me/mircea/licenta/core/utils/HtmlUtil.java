package me.mircea.licenta.core.utils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.google.common.net.InternetDomainName;

public class HtmlUtil {
	public static final Set<String> htmlTags = new HashSet<>();
	static {
		try {
			String text = new String(Files.readAllBytes(Paths.get("/html_tags.csv")), StandardCharsets.UTF_8);
			Arrays.asList(text.split(",")).stream().forEach(htmlTags::add);
		} catch (IOException e) {
			//TODO: logger
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

	public static Document sanitizeHtml(Document doc) {
		doc.select("nav,footer,script,noscript,style").remove();
		doc.getElementsByAttribute("style").removeAttr("style");
		doc.getElementsByAttributeValueContaining("class", "carousel").remove();
		doc.getElementsByAttributeValueContaining("class", "promo").remove();
		doc.getElementsByAttributeValueContaining("class", "header").remove();
		doc.getElementsByAttributeValueContaining("class", "nav").remove();
		
		return doc;
	}
	
	public static Element extractMainContent(Document doc) {
		sanitizeHtml(doc);
		
		//TODO: fix this selector
		return doc.select("[id='content'],[class*='continut'],[class*='page']:not(:has([id='content'],[class*='continut'],[class*='page']))").first();
	}
}
