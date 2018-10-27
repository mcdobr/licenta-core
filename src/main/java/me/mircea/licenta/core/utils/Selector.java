package me.mircea.licenta.core.utils;

public class Selector {
	//TODO: finish this
	public enum SelectorMethod {
		CSS,
		REGEX,
		XPATH
	}
	
	public enum SelectorType {
		ELEMENT,
		TEXT,
		ATTRIBUTE,
		LINK
	}
	
	private SelectorMethod method;
	private SelectorType type;
	private String value;
	private String attr;

}
