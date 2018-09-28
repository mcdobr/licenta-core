package me.mircea.licenta.core.infoextraction;

import java.time.LocalDate;
import java.util.Locale;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import me.mircea.licenta.core.entities.PricePoint;
import me.mircea.licenta.core.entities.Book;
import me.mircea.licenta.core.entities.Site;

/**
 * @author mircea
 */
public interface InformationExtractionStrategy {
	/**
	 * @brief Select all leaf nodes that look like a product.
	 * @param doc
	 * @return
	 * @throws NullPointerException
	 *             if the passed document is null.
	 */
	public Elements extractProductHtmlElements(Document doc);

	/**
	 * @brief Extracts a product from the given html element.
	 * @param htmlElement
	 * @param retrievedTime
	 * @param site
	 * @return HTML elements that contain products.
	 * @throws NullPointerException
	 *             if the passed document is null.
	 */
	public Book extractBook(Element htmlElement, Document productPage);
	
	public default Book extractBook(Element htmlElement) {
		return extractBook(htmlElement, null);
	}

	public PricePoint extractPricePoint(Element htmlElement, Locale locale, LocalDate retrievedDay, Site site);

	/**
	 * @param productPage
	 * @return The description of the product on that page.
	 * @throws NullPointerException
	 *             if the passed document is null.
	 */
	public String extractProductDescription(Document productPage);

	/**
	 * @param page - a document to be analyzed.
	 * @return
	 */
	public boolean hasProducts(Document page);
}
