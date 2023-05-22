package eu.europa.ec.comp.elen.notice.common.xml;

import org.w3c.dom.Document;

import javax.xml.XMLConstants;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.OutputStream;

public class XmlDocumentPersister {
    public void serializeDocument(Document document, OutputStream os)
            throws TransformerException {
        var transformerFactory = TransformerFactory.newInstance();

        // prohibit the use of all protocols by external entities:
        transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");

        transformerFactory.setAttribute("indent-number", 2);
        var transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        var source = new DOMSource(document);
        StreamResult result = new StreamResult(os);
        transformer.transform(source, result);
    }
}
