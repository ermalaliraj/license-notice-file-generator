package eu.europa.ec.comp.elen.notice.backend.xml;

public class DependencyText {

    private String text;
    private int lastIndex;

    public DependencyText(String text, int lastIndex) {
        this.text = text;
        this.lastIndex = lastIndex;
    }

    public String getText() {
        return text;
    }

    public int getLastIndex() {
        return lastIndex;
    }
}
