package me.mircea.licenta.core.entities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Currency;
import java.util.Locale;
import java.util.Optional;

import org.junit.Test;

public class BookTest {
	
	@Test
	public void shouldBeEqual() {
		Book book1 = new Book();
		Book book2 = new Book();
		
		assertEquals(book1, book2);
		
		book1.setIsbn("978-1234-1093-23");
		book2.setIsbn("978 -1234-1093-23");
		
		assertEquals(book1, book2);
		
		PricePoint price1 = new PricePoint(1, BigDecimal.valueOf(20.00), null, LocalDate.now(), null, null);
		PricePoint price2 = new PricePoint(1, BigDecimal.valueOf(20.00), null, LocalDate.now(), null, null);
		
		book1.getPricepoints().add(price1);
		book2.getPricepoints().add(price2);
		
		assertEquals(book1, book2);
	}
	
	@Test
	public void shouldMergeForMostInformation() {
		Book persisted = new Book(1, "Anna Karenina", "Limba de lemn", Arrays.asList("Lev Tolstoi"));
		Book addition = new Book(null, "Anna Karenina", "Si mai multa limba de lemn", Arrays.asList("Lev Tolstoi"));
	
		Optional<Book> mergeOperation = Book.merge(persisted, addition);
		assertTrue(mergeOperation.isPresent());	
		Book merged = mergeOperation.get();
		assertEquals("Si mai multa limba de lemn", merged.getDescription());
		
		addition.setIsbn("1234567890123");
		
		mergeOperation = Book.merge(persisted, addition);
		assertTrue(mergeOperation.isPresent());	
		merged = mergeOperation.get();
		assertEquals("1234567890123", merged.getIsbn());
		
		persisted.setIsbn("0987654321098");
		mergeOperation = Book.merge(persisted, addition);
		assertFalse(mergeOperation.isPresent());
	}
	
	@Test
	public void shouldHaveOnlyOnePricepointPerSiteDay() {
		Book persisted = new Book(1, "Anna Karenina", "Limba de lemn", Arrays.asList("Lev Tolstoi"));
		Book addition = new Book(null, "Anna Karenina", "Si mai multa limba de lemn", Arrays.asList("Lev Tolstoi"));
		
		Site site = new Site(1, "alexa", "https://someurl.com");
		
		final Currency ron = Currency.getInstance(Locale.forLanguageTag("ro-ro"));
		PricePoint p1 = new PricePoint(BigDecimal.valueOf(30.53), ron, LocalDate.now(), null, site);
		PricePoint p2 = new PricePoint(BigDecimal.valueOf(30.53), ron, LocalDate.now(), null, site);

		PricePoint p3 = new PricePoint(BigDecimal.valueOf(30.53), ron, LocalDate.now().plusDays(1), null, site);
		
		persisted.getPricepoints().add(p1);
		persisted.getPricepoints().add(p3);
		addition.getPricepoints().add(p2);
		
		Optional<Book> mergeOperation = Book.merge(persisted, addition);
		assertTrue(mergeOperation.isPresent());
		Book merged = mergeOperation.get();
		
		assertEquals(2, merged.getPricepoints().size());
	}
}
