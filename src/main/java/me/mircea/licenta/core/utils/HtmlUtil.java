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
		doc.getElementsByTag("style").remove();
		doc.getElementsByTag("script").remove();
		doc.getElementsByAttribute("style").removeAttr("style");
		doc.getElementsByAttributeValueContaining("class", "carousel").remove();
		return doc;
	}
}
