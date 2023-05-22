package eu.europa.ec.comp.elen.notice.common;

import eu.europa.ec.comp.elen.notice.backend.xml.MavenXmlCopyrightsMapping;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;

public class PathRetriever {
    public Path fromClasspath(String resourcePath) throws URISyntaxException {
        final URL resource = MavenXmlCopyrightsMapping.class.getResource(resourcePath);
        if (null == resource) {
            throw new IllegalArgumentException(
                    String.format("Cannot find classpath resource with name: '%s'", resourcePath));
        }
        return Path.of(resource.toURI());
    }
}
