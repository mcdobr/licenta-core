package me.mircea.licenta.core.utils;

import java.net.MalformedURLException;
import java.net.URL;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.google.common.net.InternetDomainName;

public class HtmlUtil {
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
	
	public static Document extractMainContent(Document doc) {
		doc = (Document) doc.select("[id='content'],[class*='continut'],[class*='page']:not(:has([id='content'],[class*='continut'],[class*='page']))").first();
		return sanitizeHtml(doc);
	}
}
