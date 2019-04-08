package me.mircea.licenta.core.parser.utils;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EntityNormalizerTest {

    @Test
    public void shouldRemoveRomanianStopwords() {
        Set<String> mySet = new HashSet<>(Arrays.asList("creangă",
                "editura",
                "fzh",
                "amintiri",
                "din",
                "9789738007161",
                "paperback",
                "ion",
                "copilărie",
                "tedit",
                "școrect",
                "şgreșit",
                "țcorect",
                "ţgreșit"
        ));

        EntityNormalizer romanianNormalizer = new EntityNormalizer(Locale.forLanguageTag("ro"));
        Set<String> keywords = romanianNormalizer.splitKeywords(mySet);

        assertFalse(keywords.contains("din"));
        assertTrue(keywords.contains("copilarie"));
    }

    @Test
    public void shouldRemoveRomanianSpecialCharacters() {
        Set<String> mySet = new HashSet<>(Arrays.asList(
                "școrect",
                "şgreșit",
                "țcorect",
                "ţgreșit",
                "ăceva",
                "âaltceva",
                "îltceva"
        ));


        EntityNormalizer romanianNormalizer = new EntityNormalizer(Locale.forLanguageTag("ro"));
        Set<String> keywords = romanianNormalizer.splitKeywords(mySet);

        assertTrue(keywords.contains("sgresit"));
        assertTrue(keywords.contains("scorect"));
        assertTrue(keywords.contains("tcorect"));
        assertTrue(keywords.contains("tgresit"));
        assertTrue(keywords.contains("aceva"));
        assertTrue(keywords.contains("aaltceva"));
        assertTrue(keywords.contains("iltceva"));
    }

}
