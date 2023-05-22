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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class XmlLicenseMappings extends BaseXmlLoader {

    // name follows format groupId:artifactId like org.apache.tomcat.embed:tomcat-embed-core
    private final Map<String, List<String>> nameToLicense = new HashMap<>();

    /**
     * Load license mapping. Order of paths matters, as latest data of a library override previous one.
     *
     * @param xmlPaths
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    public XmlLicenseMappings(Path... xmlPaths) throws ParserConfigurationException, IOException, SAXException {
        Objects.requireNonNull(xmlPaths);
        if (0 == xmlPaths.length) {
            throw new IllegalArgumentException("xmlPaths cannot be empty");
        }
        for (Path xmlPath : xmlPaths) {
            final List<MappedLicense> mappedLicenses = loadMappings(xmlPath);
            mappedLicenses.forEach(mappedLicense ->
                    nameToLicense.put(mappedLicense.completeName(),
                            Collections.unmodifiableList(mappedLicense.getLicenseAliases())));
        }
    }

    /**
     * @param groupId
     * @param artifactId
     * @return The list of mapped license aliases if any, empty list otherwise.
     */
    public List<String> getLicenseAliases(String groupId, String artifactId) {
        Objects.requireNonNull(groupId);
        Objects.requireNonNull(artifactId);
        final List<String> licenses = this.nameToLicense.get(MappedLicense.completeName(groupId, artifactId));
        if (null == licenses) {
            return Collections.emptyList();
        }
        return licenses;
    }

    private List<MappedLicense> loadMappings(Path xmlFile)
            throws ParserConfigurationException, IOException, SAXException {
        final Document document = parseXmlFile(xmlFile);
        final Optional<Element> licenseLookup = getLicenseLookup(document);
        if (licenseLookup.isEmpty()) {
            log.warn("No <licenseLookup/> found inside file: '{}'", xmlFile);
        }
        return licenseLookup
                .map(this::extractArtifacts)
                .orElseGet(Collections::emptyList);
    }

    private List<MappedLicense> extractArtifacts(Element root) {
        final List<Element> artifacts = getChildElements(root, "artifact");
        return artifacts
                .stream()
                .map(this::toMappedLicense)
//                .peek(mappedLicense -> System.out.println("Got: " + mappedLicense))
//                .toList()
                .collect(Collectors.toList());
    }

    private MappedLicense toMappedLicense(Element artifact) {
        final String groupId = getChildText(artifact, "groupId");
        final String artifactId = getChildText(artifact, "artifactId");
        final List<String> licenses =
                getChildElements(artifact, "license")
                        .stream()
                        .map(Node::getTextContent)
//                        .toList();
                        .collect(Collectors.toList());

        return new MappedLicense(groupId, artifactId, licenses);
    }

    private Optional<Element> getLicenseLookup(Document doc) {
        final NodeList rootList = doc.getChildNodes();
        if (rootList.getLength() == 0) {
            return Optional.empty();
        }
        for (int i = 0; i < rootList.getLength(); i++) {
            final Node item = rootList.item(0);
            if (item.getNodeType() == Node.ELEMENT_NODE && item.getNodeName().equals("license-lookup")) {
                return Optional.of((Element) item);
            }
        }
        return Optional.empty();
    }
}
