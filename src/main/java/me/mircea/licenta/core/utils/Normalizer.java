package me.mircea.licenta.core.utils;

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
}
