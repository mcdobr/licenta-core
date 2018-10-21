package me.mircea.licenta.core.infoextraction;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import me.mircea.licenta.core.entities.WebWrapper;

public interface WrapperGenerationStrategy {
	public WebWrapper getWrapper(Element bookPage);
	public String generateCssSelectorFor(Elements elements);
}
