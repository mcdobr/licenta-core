package me.mircea.licenta.core.entities;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.Test;

public class ProductTest {
	@Test
	public void shouldBeEqual() {
		PricePoint price1 = new PricePoint(1, BigDecimal.valueOf(20.00), null, LocalDate.now(), null);
		PricePoint price2 = new PricePoint(1, BigDecimal.valueOf(20.00), null, LocalDate.now(), null);
	
		Product product1 = new Product();
		Product product2 = new Product();
		
		assertEquals(product1, product2);
	}
}
