package me.mircea.licenta.core.utils;

import java.util.Set;
import java.util.stream.Collectors;

public class CssUtil {
	//TODO: convert to Filtering class?
	private CssUtil() {
	}
	
	public static String makeLeafOfSelector(final String selector) {
		return String.format("%s:not(:has(%s))", selector, selector);
	}
	
	public static String makeClassOrIdContains(final Set<String> words) {
		return String.join(",", words.stream()
				.map(word -> String.format("[class*='%s'],[id*='%s']", word, word)).
				collect(Collectors.toList()));
	}
}
