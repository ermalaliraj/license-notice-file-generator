package eu.europa.ec.comp.elen.notice.common.xml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Abstract base loader of XML content. Provides utility functions for processing the XML content.
 */
public abstract class BaseXmlLoader {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected static Document parseXmlFile(Path xmlFile) throws ParserConfigurationException, SAXException,
            IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        // prohibit the use of all protocols by external entities:
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        factory.setNamespaceAware(true);
        DocumentBuilder db = factory.newDocumentBuilder();
        return db.parse(xmlFile.toFile());
    }

    protected String getChildText(Element element, String tagName) {
        return findChildText(element, tagName)
                .orElseThrow(() -> exceptionForMissingTag(tagName, element.getTagName()));
    }

    protected static MissingXmlTagException exceptionForMissingTag(String tagName, String parentTagName) {
        return new MissingXmlTagException(tagName, parentTagName);
    }

    protected List<Element> getChildElements(Element element, String tagName) {
        final NodeList nodeList = element.getElementsByTagName(tagName);
        var elements = new ArrayList<Element>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            elements.add((Element) nodeList.item(i));
        }
        return elements;
    }

    protected Element getChildElement(Element element, String tagName) {
        return findChildElement(element, tagName).orElseThrow(
                () -> exceptionForMissingTag(tagName, element.getTagName()));
    }

    protected Optional<Element> findChildElement(Element element, String tagName) {
        final NodeList nodeList = element.getElementsByTagName(tagName);
        if (nodeList.getLength() == 1) {
            final Node item = nodeList.item(0);
            if (item.getNodeType() == Node.ELEMENT_NODE) {
                return Optional.of((Element) item);
            }
        } else if ("name".equals(tagName)) {
            // special case when tagName is 'name'
            final NodeList childNodes = element.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                final Node item = childNodes.item(i);
                if (item.getNodeType() == Node.ELEMENT_NODE && "name".equals(item.getNodeName())) {
                    return Optional.of((Element) item);
                }
            }
        }
        return Optional.empty();
    }

    protected Optional<String> findChildText(Element element, String tagName) {
        return findChildElement(element, tagName).map(Node::getTextContent);
    }
}
