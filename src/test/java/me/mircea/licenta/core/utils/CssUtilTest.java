package me.mircea.licenta.core.utils;

import static org.junit.Assert.assertEquals;

import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

import me.mircea.licenta.core.parser.utils.CssUtil;

public class CssUtilTest {
	@Test
	public void shouldCreateSelectorFromWordlistRight() {
		Set<String> dummySet = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
		dummySet.add("a");
		dummySet.add("b");
		
		String cssSelector = CssUtil.makeClassOrIdContains(dummySet);
		System.out.println(cssSelector);
		assertEquals("[class*='a'],[id*='a'],[class*='b'],[id*='b']", cssSelector);
	}
}
