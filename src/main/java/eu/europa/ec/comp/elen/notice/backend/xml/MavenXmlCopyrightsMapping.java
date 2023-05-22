package eu.europa.ec.comp.elen.notice.backend.xml;

import eu.europa.ec.comp.elen.notice.common.PathRetriever;
import eu.europa.ec.comp.elen.notice.common.xml.BaseXmlLoader;
import eu.europa.ec.comp.elen.notice.frontend.xml.CopyrightsAndLicenseAliases;
import eu.europa.ec.comp.elen.notice.frontend.xml.CopyrightsResult;
import eu.europa.ec.comp.elen.notice.frontend.xml.XmlArtifact;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Provide copyrights and license aliases information based on groupId, artifactId and version or a Java dependency.
 */
public class MavenXmlCopyrightsMapping extends BaseXmlLoader {

    private final Map<String, CopyrightsAndLicenseAliases> groupIds = new HashMap<>();
    private final Map<String, CopyrightsAndLicenseAliases> groupIdAndArtifactIds = new HashMap<>();
    private final Map<String, CopyrightsAndLicenseAliases> groupIdAndArtifactIdAndVersions = new HashMap<>();

    public  MavenXmlCopyrightsMapping(Path copyrightsXml)
            throws ParserConfigurationException, IOException, SAXException {
        final Document document = parseXmlFile(copyrightsXml);
        final Optional<Element> copyrightsLookup = findCopyrightsLookup(document);
        copyrightsLookup.ifPresentOrElse(
                this::processRootElement,
                logWarnForMissingRootElement(copyrightsXml)
        );
    }

    private Runnable logWarnForMissingRootElement(Path copyrightsXml) {
        return () -> log.warn("No <copyrights-lookup/> found inside file: '{}'", copyrightsXml);
    }

    private void processRootElement(Element element) {
        extractXmlArtifacts(element)
                .forEach(this::populateMaps);
    }

    public Optional<CopyrightsResult> match(String groupId, String artifactId, String version) {
        final String fullName = groupIdAndArtifactIdAndVersion(groupId, artifactId, version);
        if (groupIdAndArtifactIdAndVersions.containsKey(fullName)) {
            final CopyrightsAndLicenseAliases data = groupIdAndArtifactIdAndVersions.get(fullName);
            return Optional.of(new CopyrightsResult(data.getCopyrights(), data.getLicenseAliases(), MatchLevel.FULL));
        } else {
            String groupIdAndArtifactId = groupIdAndArtifactId(groupId, artifactId);
            if (groupIdAndArtifactIds.containsKey(groupIdAndArtifactId)) {
                final CopyrightsAndLicenseAliases data = groupIdAndArtifactIds.get(groupIdAndArtifactId);
                return Optional.of(new CopyrightsResult(data.getCopyrights(), data.getLicenseAliases(),
                        MatchLevel.GROUP_ID_AND_ARTIFACT_ID));
            } else if (groupIds.containsKey(groupId)) {
                final CopyrightsAndLicenseAliases data = groupIds.get(groupId);
                return Optional.of(new CopyrightsResult(data.getCopyrights(), data.getLicenseAliases(), MatchLevel.GROUP_ID));
            }
        }
        return Optional.empty();
    }

    private void populateMaps(XmlArtifact xmlArtifact) {
        groupIds.putIfAbsent(xmlArtifact.getGroupId(), xmlArtifact.getCopyrightsAndLicenseAliases());
        xmlArtifact.getArtifactIds()
                   .stream()
                   .map(artifactId -> groupIdAndArtifactId(xmlArtifact.getGroupId(), artifactId))
                   .forEach(groupIdAndArtifactId -> {
                       groupIdAndArtifactIds.putIfAbsent(groupIdAndArtifactId,
                               xmlArtifact.getCopyrightsAndLicenseAliases());
                       var groupIdAndArtifactIdAndVersion = String.format("%s:%s", groupIdAndArtifactId,
                               xmlArtifact.getVersion());
                       groupIdAndArtifactIdAndVersions.put(groupIdAndArtifactIdAndVersion,
                               xmlArtifact.getCopyrightsAndLicenseAliases());
                   });
    }

    private static String groupIdAndArtifactId(String groupId, String artifactId) {
        return String.format("%s:%s", groupId, artifactId);
    }

    private static String groupIdAndArtifactIdAndVersion(String groupId, String artifactId, String version) {
        return String.format("%s:%s:%s", groupId, artifactId, version);
    }

    private List<XmlArtifact> extractXmlArtifacts(Element element) {
        return getChildElements(element, "artifact")
                .stream()
                .map(this::convertXml)
//                .toList();
                .collect(Collectors.toList());
    }

    private XmlArtifact convertXml(Element xmlArtifact) {
        String groupId = getGroupId(xmlArtifact);
        List<String> artifactIds = getArtifactIds(xmlArtifact);
        String version = getVersion(xmlArtifact);
        List<String> copyrights = getCopyrights(xmlArtifact);
        List<String> licenseAliases = getLicenseAliases(xmlArtifact);

        return new XmlArtifact(groupId, artifactIds, version,
                new CopyrightsAndLicenseAliases(copyrights, licenseAliases));
    }

    private List<String> getLicenseAliases(Element xmlArtifact) {
        return getChildElements(xmlArtifact, "license")
                .stream()
                .map(license -> getChildText(license, "name").trim())
                .distinct()
//                .toList();
                .collect(Collectors.toList());
    }

    private String getVersion(Element xmlArtifact) {
        return getChildText(xmlArtifact, "version");
    }

    private List<String> getArtifactIds(Element xmlArtifact) {
        return getChildElements(xmlArtifact, "artifactId")
                .stream()
                .map(element -> element.getTextContent().trim())
                .distinct()
//                .toList();
                .collect(Collectors.toList());
    }

    private List<String> getCopyrights(Element xmlArtifact) {
        return getChildElements(xmlArtifact, "copyright")
                .stream()
                .map(element -> element.getTextContent().trim())
                .distinct()
//                .toList();
                .collect(Collectors.toList());
    }

    private String getGroupId(Element xmlArtifact) {
        return getChildText(xmlArtifact, "groupId");
    }

    private Optional<Element> findCopyrightsLookup(Document doc) {
        final NodeList nodeList = doc.getChildNodes();
        if (nodeList.getLength() == 0) {
            return Optional.empty();
        }
        if (nodeList.getLength() > 0) {
            for (int i = 0; i < nodeList.getLength(); i++) {
                final Node item = nodeList.item(i);
                if (item.getNodeType() == Node.ELEMENT_NODE && item.getNodeName().equals("copyrights-lookup")) {
                    return Optional.of((Element) item);
                }
            }
        }

        return Optional.empty();
    }
    
    public static void main(String[] args)
            throws URISyntaxException, ParserConfigurationException, IOException, SAXException {
        final Path xmlPath = new PathRetriever().fromClasspath("/maven-copyrights-lookup.xml");
        new MavenXmlCopyrightsMapping(xmlPath);
    }
}
