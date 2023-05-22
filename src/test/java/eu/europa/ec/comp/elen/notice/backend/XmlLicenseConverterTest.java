package eu.europa.ec.comp.elen.notice.backend;

import eu.europa.ec.comp.elen.notice.BaseTest;
import eu.europa.ec.comp.elen.notice.backend.xml.XmlLibrary;
import eu.europa.ec.comp.elen.notice.backend.xml.XmlLicenseConverter;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class XmlLicenseConverterTest extends BaseTest {
    @Test
    void loadOneLibrary() throws URISyntaxException, ParserConfigurationException, IOException, SAXException {
        Path xmlFile = getPathFromClasspath("/licenses_1.xml");
        XmlLicenseConverter converter = new XmlLicenseConverter();

        final List<XmlLibrary> xmlLibraries = converter.convertXml(xmlFile);

        assertNotNull(xmlLibraries);
        assertEquals(1, xmlLibraries.size());
        assertEquals("antlr", first(xmlLibraries).getGroupId());
        assertEquals("antlr", first(xmlLibraries).getArtifactId());
        assertEquals("2.7.7", first(xmlLibraries).getVersion());
        assertEquals(1, first(xmlLibraries).getLicenses().size());
        assertEquals("BSD License", first(first(xmlLibraries).getLicenses()).getName());
    }

    @Test
    void loadTwoLibraries() throws URISyntaxException, ParserConfigurationException, IOException, SAXException {
        Path xmlFile = getPathFromClasspath("/licenses_2.xml");
        XmlLicenseConverter converter = new XmlLicenseConverter();

        final List<XmlLibrary> xmlLibraries = converter.convertXml(xmlFile);

        assertNotNull(xmlLibraries);
        assertEquals(2, xmlLibraries.size());
        // check first entry
        assertEquals("antlr", first(xmlLibraries).getGroupId());
        assertEquals("antlr", first(xmlLibraries).getArtifactId());
        assertEquals("2.7.7", first(xmlLibraries).getVersion());
        assertEquals(1, first(xmlLibraries).getLicenses().size());
        // check second entry
        assertEquals("asm", second(xmlLibraries).getGroupId());
        assertEquals("asmx", second(xmlLibraries).getArtifactId());
        assertEquals("3.3.1", second(xmlLibraries).getVersion());
        assertEquals(3, second(xmlLibraries).getLicenses().size());

    }
}