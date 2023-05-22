package eu.europa.ec.comp.elen.notice;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;

public abstract class BaseTest {
    protected Path getPathFromClasspath(String name) throws URISyntaxException {
        return Path.of(getClass().getResource(name).toURI());
    }

    protected static <T> T first(List<T> xmlLibraries) {
        return xmlLibraries.get(0);
    }

    protected static <T> T second(List<T> xmlLibraries) {
        return xmlLibraries.get(1);
    }
}
