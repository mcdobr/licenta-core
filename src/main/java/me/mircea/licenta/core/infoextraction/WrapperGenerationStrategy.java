package me.mircea.licenta.core.infoextraction;

import java.util.Set;

import me.mircea.licenta.core.entities.WebWrapper;

public interface WrapperGenerationStrategy {
	public WebWrapper getWrapper();
	public String generateCssSelectorFor(Set<String> wordSet);
}
