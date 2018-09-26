package me.mircea.licenta.core.entities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.Locale;

import org.junit.Test;

public class PricePointTest {
	@Test
	public void shouldBeEqual() {
		Currency ron = Currency.getInstance(Locale.forLanguageTag("ro-ro"));
		Currency ron2 = Currency.getInstance(Locale.forLanguageTag("ro-ro"));
		assertEquals(ron, ron2);
		
		PricePoint p1 = new PricePoint(1, BigDecimal.valueOf(20.05), ron, LocalDate.now(), null);
		PricePoint p2 = new PricePoint(2, BigDecimal.valueOf(20.05), ron, LocalDate.now(), null);
		assertEquals(p1, p2);
		
		PricePoint p3 = new PricePoint(2, BigDecimal.valueOf(20.05), ron, LocalDate.now().plusDays(1), null);
		assertNotEquals(p1, p3);
	}
	
	@Test
	public void shouldCompareCorrectly() {
		Currency ron = Currency.getInstance(Locale.forLanguageTag("ro-ro"));
		PricePoint p1 = new PricePoint(1, BigDecimal.valueOf(20.05), ron, LocalDate.now(), null);
		PricePoint p2 = new PricePoint(2, BigDecimal.valueOf(20.05), ron, LocalDate.now().plusDays(1), null);
		
		assertTrue(p1.compareTo(p2) < 0);
	}
}
