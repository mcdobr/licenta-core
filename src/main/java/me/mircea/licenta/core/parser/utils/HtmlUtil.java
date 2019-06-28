package me.mircea.licenta.core.parser.utils;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.net.InternetDomainName;
import org.apache.commons.io.IOUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class HtmlUtil {
    private static final Logger logger = LoggerFactory.getLogger(HtmlUtil.class);
    private static final Set<String> htmlTags = new HashSet<>();

    private static final ClassLoader classLoader = HtmlUtil.class.getClassLoader();
    private static final String CSS_CLASS_ATTRIBUTE = "class";

    static {
        try {
            InputStream is = classLoader.getResourceAsStream("htmlTags.csv");
            String text = IOUtils.toString(is, StandardCharsets.UTF_8);

            htmlTags.addAll(Arrays.asList(text.split(",")));
        } catch (IOException e) {
            logger.error("Could not read file containing all html compliant tags: {}", e);
        }
    }

    private HtmlUtil() {

    }

    public static String getDomainOfUrl(String url) throws MalformedURLException {
        return InternetDomainName.from(new URL(url).getHost()).topPrivateDomain().toString();
    }

    public static Element extractMainContent(Document doc) {
        sanitizeHtml(doc);

        //TODO: fix this selector (not applies only on last one)
        return doc.select("[id='content'],[class*='continut'],[class*='page']:not(:has([id='content'],[class*='continut'],[class*='page']))").first();
    }


    /**
     * @param doc document to be parsed
     * @return Document from which irrelevant items have been removed.
     */
    public static Document sanitizeHtml(Document doc) {
        final String joinedHtmlElementsToBeRemoved = "nav,footer,script,noscript,style";
        doc.select(joinedHtmlElementsToBeRemoved).remove();
        doc.getElementsByAttribute("style").removeAttr("style");
        doc.getElementsByAttributeValueContaining(CSS_CLASS_ATTRIBUTE, "carousel").remove();
        doc.getElementsByAttributeValueContaining(CSS_CLASS_ATTRIBUTE, "promo").remove();
        doc.getElementsByAttributeValueContaining(CSS_CLASS_ATTRIBUTE, "header").remove();
        doc.getElementsByAttributeValueContaining(CSS_CLASS_ATTRIBUTE, "banner").remove();
        return doc;
    }

    public static Optional<String> getCanonicalUrl(Element doc) {
        Element canonicalLink = doc.selectFirst("link[rel='canonical']");

        String result = null;
        if (canonicalLink != null) {
            result = canonicalLink.absUrl("href");
        } // TODO: add option for meta tag with canonical link

        return Optional.ofNullable(result);
    }
}
