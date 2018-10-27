package me.mircea.licenta.core.infoextraction;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Map;

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
	 * @brief Select all the product cards on a multiproduct page.
	 * @param doc
	 * @return
	 * @throws NullPointerException
	 *             if the passed document is null.
	 */
	public Elements extractBookCards(Document multiBookPage);

	/**
	 * @brief Extracts a product from the given book card.
	 * @param bookCard
	 * @param retrievedTime
	 * @param site
	 * @return HTML elements that contain products.
	 * @throws NullPointerException
	 *             if the passed document is null.
	 */
	public Book extractBook(Element bookCard, Document bookPage);
	
	public default Book extractBook(Element bookCard) {
		return extractBook(bookCard, null);
	}

	public PricePoint extractPricePoint(Element bookCard, Locale locale, LocalDate retrievedDay, Site site);

	public Map<String, String> extractBookAttributes(Document bookPage);
	
	/**
	 * @param bookPage
	 * @return The description of the product on that page.
	 * @throws NullPointerException
	 *             if the passed document is null.
	 */
	public String extractBookDescription(Document bookPage);

}
