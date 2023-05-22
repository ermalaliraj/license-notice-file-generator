package eu.europa.ec.comp.elen.notice.common.xml;

public class MissingXmlTagException extends RuntimeException {
    public MissingXmlTagException(String missingTag, String parentTagName) {
        super(String.format("Invalid XML - Missing element <%s> for parent element <%s>", missingTag, parentTagName));
    }
}
