package me.mircea.licenta.core.parser.utils;

import com.google.common.base.Preconditions;

import java.text.Collator;
import java.text.Normalizer;
import java.util.*;

public class EntityNormalizer {
    private final Locale locale;
    private final List<String> stopwords;

    public EntityNormalizer(Locale locale) {
        Preconditions.checkNotNull(locale);
        this.locale = locale;

        ResourceBundle rb = ResourceBundle.getBundle("lexicon", locale, this.getClass().getClassLoader());
        this.stopwords = Arrays.asList(rb.getString("stopwords").split(","));
    }

    public String getLongestOfNullableStrings(final String a, final String b) {
        if (a == null && b == null)
            return null;
        if (a == null || b == null)
            return (a != null) ? a : b;
        return (a.length() >= b.length()) ? a : b;
    }

    public Object getNotNullIfPossible(final Object a, final Object b) {
        if (a == null && b == null)
            return null;
        return (a != null) ? a : b;
    }

    public Set<String> splitKeywords(Collection<String> strings) {
        Preconditions.checkNotNull(strings);
        strings.removeIf(Objects::isNull);

        Collator collator = Collator.getInstance(this.locale);
        collator.setStrength(Collator.PRIMARY);
        Set<String> keywords = new TreeSet<>(collator);
        for (String value : strings) {
            splitValueIntoKeywords(keywords, value);
        }

        // Remove stopwords
        for (String stopword : stopwords) {
            keywords.remove(stopword);
        }

        return keywords;
    }

    private void splitValueIntoKeywords(final Set<String> keywords, final String value) {
        StringBuilder sb = new StringBuilder();

        int offset = 0;
        while (offset < value.length()) {
            int codepoint = value.codePointAt(offset);
            if (Character.isLetterOrDigit(codepoint)) {
                codepoint = Character.toLowerCase(codepoint);
                sb.appendCodePoint(codepoint);
            } else {
                addLocaleReducedForm(keywords, sb);
            }

            offset += Character.charCount(codepoint);
        }

        addLocaleReducedForm(keywords, sb);
    }

    private void addLocaleReducedForm(final Set<String> keywords, final StringBuilder sb) {
        if (sb.length() > 0) {
            String str = Normalizer.normalize(sb.toString(), Normalizer.Form.NFKD);
            str = str.replaceAll("\\p{M}", "");
            keywords.add(str);

            sb.setLength(0);
        }
    }

    public String getCorrectlyFormattedIsbnIfPossible(String a, String b) {
        if (a == null && b == null) {
            return null;
        }

        if (a == null || b == null) {
            return (a != null) ? a : b;
        }

        final int ISBN13_LENGTH = 13;
        final int ISBN10_LENGTH = 10;

        if (a.length() == ISBN13_LENGTH) {
            return a;
        }
        if (b.length() == ISBN13_LENGTH) {
            return b;
        }

        if (a.length() == ISBN10_LENGTH) {
            return a;
        }
        if (b.length() == ISBN10_LENGTH) {
            return b;
        }

        return a;
    }
}
