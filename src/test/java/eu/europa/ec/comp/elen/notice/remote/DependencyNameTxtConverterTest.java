package eu.europa.ec.comp.elen.notice.remote;

import eu.europa.ec.comp.elen.notice.common.NpmJsDependencyNameTxtConverter;
import eu.europa.ec.comp.elen.notice.common.NpmJsDependencyName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DependencyNameTxtConverterTest {

    private NpmJsDependencyNameTxtConverter converter;

    @BeforeEach
    void setup() {
        this.converter = new NpmJsDependencyNameTxtConverter();
    }

    @Test
    void nameType1() {
        final NpmJsDependencyName dependencyName = converter.convert("d3-geo@2.0.2");

        assertEquals("-", dependencyName.getNamespace());
        assertEquals("d3-geo", dependencyName.getName());
        assertEquals("2.0.2", dependencyName.getVersion());
    }

    @Test
    void nameType2() {
        final NpmJsDependencyName dependencyName = converter.convert("@foliojs-fork/linebreak@1.1.1");

        assertEquals("@foliojs-fork", dependencyName.getNamespace());
        assertEquals("linebreak", dependencyName.getName());
        assertEquals("1.1.1", dependencyName.getVersion());
    }
}