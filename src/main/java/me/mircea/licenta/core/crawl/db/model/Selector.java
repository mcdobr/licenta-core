package me.mircea.licenta.core.crawl.db.model;

import org.jsoup.nodes.Element;

public class Selector {
    private String name;
    private SelectorType type;
    private String query;
    private String target;
    private boolean multiple;

    public Selector() {}

    public String selectFirstFromElement(Element htmlElement) {
        String result = null;

        Element element = htmlElement.selectFirst(this.getQuery());
        if (element != null) {
            SelectorType typeOfSelector = getType();
            switch (typeOfSelector) {
                case TEXT:
                    result = element.text();
                    break;
                case LINK:
                    result = element.absUrl("href");
                    break;
                case IMAGE:
                    result = element.absUrl("src");
                    break;
                case ATTRIBUTE:
                    result = element.attr(getTarget());
                    break;
                default:
                    throw new InvalidSelectorTypeException("Selector type is invalid:");
            }
        }
        return result;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SelectorType getType() {
        return type;
    }

    public void setType(SelectorType type) {
        this.type = type;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public boolean isMultiple() {
        return multiple;
    }

    public void setMultiple(boolean multiple) {
        this.multiple = multiple;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Selector{");
        sb.append("name='").append(name).append('\'');
        sb.append(", type=").append(type);
        sb.append(", query='").append(query).append('\'');
        sb.append(", target='").append(target).append('\'');
        sb.append(", multiple=").append(multiple);
        sb.append('}');
        return sb.toString();
    }
}
