package me.mircea.licenta.core.parser.utils;

import java.util.Set;
import java.util.stream.Collectors;

public class CssUtil {
    private static final String ELEMENT_DOES_NOT_CONTAIN_ELEMENT_LIKE_IT_CSS_SELECTOR_FORMAT = "%s:not(:has(%s))";
    private static final String ELEMENT_CLASS_OR_ID_CONTAINS_STRING_CSS_SELECTOR_FORMAT = "[class*='%s'],[id*='%s']";

    public static String makeLeafOfSelector(final String selector) {
        return String.format(ELEMENT_DOES_NOT_CONTAIN_ELEMENT_LIKE_IT_CSS_SELECTOR_FORMAT, selector, selector);
    }

    public static String makeClassOrIdContains(final Set<String> words) {
        return words.stream()
                .map(word -> String.format(ELEMENT_CLASS_OR_ID_CONTAINS_STRING_CSS_SELECTOR_FORMAT, word, word))
                .collect(Collectors.joining(","));
    }

    private CssUtil() {
    }
}
