package me.mircea.licenta.core.utils;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class TextContentAnalyzer {
	public static final Set<String> titleWordSet = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
	public static final Set<String> authorWordSet = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
	public static final Set<String> priceWordSet = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
	public static final Set<String> codeWordSet = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
	public static final Map<String, String> formatsWordSet = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
	public static final Set<String> yearWordSet = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
	public static final Set<String> publisherWordSet = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
	public static final Set<String> descriptionWordSet = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
	
	public static final String authorSeparatorsRegex = "[,&]\\s*";
	
	
	static {
		titleWordSet.add("titlu");
		titleWordSet.add("title");
		titleWordSet.add("nume");
		titleWordSet.add("name");
		
		authorWordSet.add("autor");
		authorWordSet.add("autori");
		authorWordSet.add("author");
		authorWordSet.add("authors");

		priceWordSet.add("pret");
		priceWordSet.add("price");
		
		codeWordSet.add("isbn");
		codeWordSet.add("cod");
		
		formatsWordSet.put("hardcover", "hardcover");
		formatsWordSet.put("paperback", "paperback");
		formatsWordSet.put("pdf", "pdf");
		formatsWordSet.put("epub", "epub");
		formatsWordSet.put("mobi", "mobi");
	
		formatsWordSet.put("cartonata", "hardcover");
		formatsWordSet.put("necartonata", "paperback");
		
		yearWordSet.add("an");
		yearWordSet.add("anul");
		yearWordSet.add("year");
		
		publisherWordSet.add("publisher");
		publisherWordSet.add("editura");
		
		descriptionWordSet.add("descriere");
		descriptionWordSet.add("description");
	}
	
	
	private TextContentAnalyzer() {
	}
	
	
}
