package me.mircea.licenta.core.parser.utils;

import java.util.List;

public class Normalizer {
	private Normalizer() {	
	}
	
	public static String getLongestOfNullableStrings(final String a, final String b) {
		if (a == null && b == null)
			return null;
		if (a == null || b == null)
			return (a != null) ? a : b;
		return (a.length() >= b.length()) ? a : b;
	}
	
	public static Object getNotNullIfPossible(final Object a, final Object b) {
		if (a == null && b == null)
			return null;
		return (a != null) ? a : b;
	}
	
	public static <E> List<E> getLongestOfLists(final List<E> a, final List<E> b) {
		return (a.size() >= b.size()) ? a : b;
	}
}
