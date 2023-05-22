package eu.europa.ec.comp.elen.notice.generator;

import org.silentsoft.oss.License;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class NoticeFileGeneratorV3 {
    private static final Logger LOG = LoggerFactory.getLogger(NoticeFileGeneratorV3.class);

    private String productName;
    private String owner;
    private List<String> texts;
    private List<LibraryNoticeV3> libraries;

    private NoticeFileGeneratorV3(NoticeFileBuilder builder) {
        this.productName = builder.productName;
        this.owner = builder.owner;
        this.texts = builder.texts;
        this.libraries = builder.libraries;
    }

    public String generate() {
        return generate(productName, owner, texts.toArray(new String[0]), libraries.toArray(new LibraryNoticeV3[0]));
    }

    public static NoticeFileBuilder newInstance(String productName) {
        return new NoticeFileBuilder(productName);
    }

    public static NoticeFileBuilder newInstance(String productName, String owner) {
        return new NoticeFileBuilder(productName, owner);
    }

    public static String generate(String productName, String owner, String[] texts, LibraryNoticeV3... libraries) {
        StringBuffer buffer = new StringBuffer();

//        buffer.append(String.format("# %s\r\n", productName));
//        if (owner != null && "".equals(owner.trim()) == false) {
//            buffer.append(String.format("Copyright (c) %s.\r\n", owner));
//        }
//        buffer.append("\r\n");

        for (String text : texts) {
            buffer.append(String.format("%s\r\n\r\n", text));
        }
        for (LibraryNoticeV3 library : libraries) {
            buffer.append(String.format("%s\r\n", library.toTxt()));
        }
        getLicenses(libraries)
                .forEach(license -> buffer.append(String.format("\r\n%s", license)));

        return buffer.toString().trim();
    }

    private static Stream<License> getLicenses(LibraryNoticeV3[] libraries) {
        return getUniqueLicenseAliases(libraries)
                .peek(alias -> {
                    if (!hasValidLicense(alias)) {
                        LOG.warn("Could not find license content for alias '" + alias + "'");
                    }
                })
                .filter(alias -> hasValidLicense(alias))
                .map(alias -> loadLicense(alias).get())
                .distinct();
//                .peek(license -> System.out.println("License: " + license.getName()));
    }

    private static Stream<String> getUniqueLicenseAliases(LibraryNoticeV3[] libraries) {
        return Stream.of(libraries)
                     .flatMap(library -> library.getLicenseAliases()
                                                .stream()
                                                .map(String::trim)
                                                .map(alias -> LicenseAliasMapping.mapIfNeeded(alias)))
                     .distinct();
    }

    private static boolean hasValidLicense(String alias) {
        return LicenseAliasMapping.hasValidLicense(alias);
    }

    private static Optional<License> loadLicense(String licenseAlias) {
        return LicenseAliasMapping.getLicense(licenseAlias);
    }

    public static class NoticeFileBuilder {
        private String productName;
        private String owner;
        private List<String> texts;
        private List<LibraryNoticeV3> libraries;

        private NoticeFileBuilder(String productName) {
            this(productName, null);
        }

        private NoticeFileBuilder(String productName, String owner) {
            this.productName = productName;
            this.owner = owner;
            this.texts = new ArrayList<>();
            this.libraries = new ArrayList<>();
        }

        public NoticeFileBuilder addText(String text) {
            this.texts.add(text);

            return this;
        }

        public NoticeFileBuilder addLibrary(LibraryNoticeV3 library) {
            this.libraries.add(library);

            return this;
        }

        public String generate() {
            return new NoticeFileGeneratorV3(this).generate();
        }
    }
}
