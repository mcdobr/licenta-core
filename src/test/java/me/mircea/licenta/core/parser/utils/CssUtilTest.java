package me.mircea.licenta.core.parser.utils;

import org.junit.Test;

import java.util.Set;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;

public class CssUtilTest {
	@Test
	public void shouldCreateSelectorFromWordlistRight() {
		Set<String> dummySet = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
		dummySet.add("a");
		dummySet.add("b");
		
		String cssSelector = CssUtil.makeClassOrIdContains(dummySet);
		assertEquals("[class*='a'],[id*='a'],[class*='b'],[id*='b']", cssSelector);
	}
}
