package me.mircea.licenta.core.infoextraction;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import me.mircea.licenta.core.entities.PricePoint;
import me.mircea.licenta.core.entities.Product;
import me.mircea.licenta.core.utils.ImmutablePair;

/**
 * @author mircea
 *
 */
public interface InformationExtractionStrategy {
	/**
	 * @brief Select all leaf nodes that look like a product.
	 * @param doc
	 * @return
	 */
	public Elements getProductElements(Document doc);

	/**
	 * @brief Extracts a product from the given html element.
	 * @param htmlElement
	 * @param retrievedTime
	 * @return HTML elements that contain products.
	 */
	public ImmutablePair<Product, PricePoint> extractProductAndPricePoint(Element htmlElement, Locale locale,
			LocalDate retrievedDay);

	/**
	 * @brief Function that extracts the attribute of a product off of its page.
	 * @param productPage
	 * @return An associative array of attributes.
	 */
	public Map<String, String> extractProductAttributes(Document productPage);
}
