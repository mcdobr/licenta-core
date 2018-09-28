package me.mircea.licenta.core.infoextraction;

import java.time.LocalDate;
import java.util.Locale;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.base.Preconditions;

import me.mircea.licenta.core.entities.PricePoint;
import me.mircea.licenta.core.entities.Book;
import me.mircea.licenta.core.entities.Site;

public class SemanticWebStrategy implements InformationExtractionStrategy {

	@Override
	public Elements extractProductHtmlElements(Document doc) {
		Preconditions.checkNotNull(doc);
		return doc.select("[itemtype$='Book'],[itemtype$='Book']");
	}

	@Override
	public Book extractBook(Element htmlElement, Document productPage) {
		Elements propElements = htmlElement.select("[itemprop]");
		
		Book book = new Book();
		for (Element item : propElements) {
			String property = item.attr("itemprop");
			String content = item.attr("content");
			
			switch (property.toLowerCase()) {
			case "name": case "title":
				book.setTitle(content);
				break;
			case "author":
				book.getAuthors().add(content);
				break;
			case "isbn":
				book.setIsbn(content);
				break;
			default:
				break;
			}
		}
		
		Element imgElement = htmlElement.select("img[src]").first();
		if (imgElement != null)
			book.setCoverUrl(imgElement.absUrl("src"));
		
		return book;
	}

	@Override
	public PricePoint extractPricePoint(Element htmlElement, Locale locale, LocalDate retrievedDay, Site site) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String extractProductDescription(Document productPage) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasProducts(Document page) {
		// TODO Auto-generated method stub
		return false;
	}

}