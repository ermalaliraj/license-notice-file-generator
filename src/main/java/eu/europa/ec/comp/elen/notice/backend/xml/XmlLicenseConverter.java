package eu.europa.ec.comp.elen.notice.backend.xml;

import eu.europa.ec.comp.elen.notice.common.xml.BaseXmlLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class XmlLicenseConverter extends BaseXmlLoader {

    public List<XmlLibrary> convertXml(Path xmlFile) throws ParserConfigurationException, IOException, SAXException {
        final Document doc = parseXmlFile(xmlFile);
        final Optional<Element> dependencies = findDependencies(doc);
        if (dependencies.isEmpty()) {
            log.warn("No <licenseSummary/> or <dependencies/> tag found inside file: '{}'", xmlFile);
        }
        return dependencies
                .map(this::extractLibraries)
                .orElseGet(Collections::emptyList);
    }

    private List<XmlLibrary> extractLibraries(Element deps) {
        var libraries = new ArrayList<XmlLibrary>();
        final NodeList childNodes = deps.getElementsByTagName("dependency");
        for (int i = 0; i < childNodes.getLength(); i++) {
            final Element dependency = (Element) childNodes.item(i);
            final String groupId = getChildText(dependency, "groupId");
            final String artifactId = getChildText(dependency, "artifactId");
            final String version = getChildText(dependency, "version");
            final Element licenses = getChildElement(dependency, "licenses");

            var xmlLicenses = extractXmlLicenses(licenses);
            var library = new XmlLibrary(groupId, artifactId, version, xmlLicenses);
            libraries.add(library);
        }
        return libraries;
    }

    private List<XmlLicense> extractXmlLicenses(Element licenses) {
        return getChildElements(licenses, "license")
                .stream()
                .map(license -> {
                    final String name = getChildText(license, "name");
                    final String url = getChildText(license, "url");
                    return findChildText(license, "file")
                            .map(file -> new XmlLicense(name, url, file))
                            .orElseGet(() -> new XmlLicense(name, url, null));
                })
//                .toList();
        .collect(Collectors.toList());
    }


    private Optional<Element> findDependencies(Document doc) {
        final NodeList rootList = doc.getChildNodes();
        if (rootList.getLength() == 0) {
            return Optional.empty();
        }
        final Node licenseSummary = rootList.item(0);

        final NodeList dependenciesNodeList = licenseSummary.getChildNodes();
        if (dependenciesNodeList.getLength() > 0) {
            for (int i = 0; i < dependenciesNodeList.getLength(); i++) {
                final Node item = dependenciesNodeList.item(i);
                if (item.getNodeType() == Node.ELEMENT_NODE && item.getNodeName().equals("dependencies")) {
                    return Optional.of((Element) item);
                }
            }
        }

        return Optional.empty();
    }
}
