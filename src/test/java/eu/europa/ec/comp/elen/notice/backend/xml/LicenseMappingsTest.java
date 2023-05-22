package eu.europa.ec.comp.elen.notice.backend.xml;

import eu.europa.ec.comp.elen.notice.BaseTest;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LicenseMappingsTest extends BaseTest {
    @Test
    void loadMappings() throws URISyntaxException, ParserConfigurationException, IOException, SAXException {
        final Path xmlFile = getPathFromClasspath("/license_mapping_1.xml");

        final XmlLicenseMappings xmlLicenseMappings = new XmlLicenseMappings(xmlFile);

        final List<String> licenses1 = xmlLicenseMappings.getLicenseAliases("antisamy-bin", "org.owasp.validator");
        assertNotNull(licenses1);
        assertEquals(1, licenses1.size());
        assertEquals("BSD license", first(licenses1));
        final List<String> licenses2 = xmlLicenseMappings.getLicenseAliases("antlr", "antlr");
        assertNotNull(licenses2);
        assertEquals(2, licenses2.size());
        assertEquals("Apache License, Version 2.0", first(licenses2));
        assertEquals("Eclipse Public License v. 1.0", second(licenses2));
    }
}