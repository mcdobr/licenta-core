package me.mircea.licenta.core.infoextraction;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.base.Preconditions;

import me.mircea.licenta.core.entities.Book;
import me.mircea.licenta.core.entities.PricePoint;
import me.mircea.licenta.core.entities.Site;
import me.mircea.licenta.core.entities.WebWrapper;

public class WrapperStrategy implements InformationExtractionStrategy {
	private WebWrapper wrapper;
	
	public WrapperStrategy(WebWrapper wrapper) {
		super();
		this.wrapper = wrapper;
	}

	@Override
	public Elements extractBookHtmlElements(Document doc) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Book extractBook(Element htmlElement, Document bookPage) {
		Preconditions.checkNotNull(bookPage);
		
		Book book = new Book();
		book.setTitle(bookPage.selectFirst(wrapper.getTitleSelector()).text());
		
		List<String> authors = Arrays.asList(bookPage.selectFirst(wrapper.getAuthorsSelector()).text().split("[,&]"));
		book.setAuthors(authors);
		
		return book;
	}

	@Override
	public PricePoint extractPricePoint(Element htmlElement, Locale locale, LocalDate retrievedDay, Site site) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String extractBookDescription(Document productPage) {
		return null;
	}

	@Override
	public boolean hasBooks(Document page) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
