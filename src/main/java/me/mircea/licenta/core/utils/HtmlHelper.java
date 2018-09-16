package me.mircea.licenta.core.utils;

import org.jsoup.nodes.Document;

/**
 * @author mircea
 * @brief Utility class that contains HTML handling functions.
 */
public class HtmlHelper {
	private HtmlHelper() {
	}
	
	public static Document sanitizeHtml(Document doc) {
		doc.getElementsByTag("style").remove();
		doc.getElementsByTag("script").remove();
		doc.getElementsByAttribute("style").removeAttr("style");
		return doc;
	}
}
