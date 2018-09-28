package me.mircea.licenta.core.entities;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.Test;

public class BookTest {
	@Test
	public void shouldBeEqual() {
		PricePoint price1 = new PricePoint(1, BigDecimal.valueOf(20.00), null, LocalDate.now(), null);
		PricePoint price2 = new PricePoint(1, BigDecimal.valueOf(20.00), null, LocalDate.now(), null);
	
		Book book1 = new Book();
		Book book2 = new Book();
		
		assertEquals(book1, book2);
	}
}
