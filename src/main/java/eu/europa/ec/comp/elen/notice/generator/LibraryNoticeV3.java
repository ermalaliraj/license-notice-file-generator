package eu.europa.ec.comp.elen.notice.generator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LibraryNoticeV3 {
    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Name can be like
     * 'org.springframework.boot:spring-boot-starter-test'
     * for Maven components, or like
     * '@angular/elements'
     * for Npm components.
     */
    private String name;
    private String version;
    private String website;
    private Set<String> licenseAliases = Collections.emptySet();
    private Set<String> copyrights = Collections.emptySet();

    public String getName() {
        return name;
    }

    public LibraryNoticeV3 withName(String name) {
        this.name = name;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public LibraryNoticeV3 withVersion(String version) {
        this.version = version;
        return this;
    }

    public String getWebsite() {
        return website;
    }

    public LibraryNoticeV3 withWebsite(String website) {
        this.website = website;
        return this;
    }

    public Collection<String> getLicenseAliases() {
        return licenseAliases;
    }

    public LibraryNoticeV3 withLicenseAlias(List<String> licenseAliases) {
        if (null != licenseAliases) {
            this.licenseAliases = new HashSet<>(licenseAliases);
        }
        return this;
    }

    public Collection<String> getCopyrights() {
        return copyrights;
    }

    public LibraryNoticeV3 withCopyrights(List<String> copyrights) {
        if (null != copyrights) {
            this.copyrights = new HashSet<>(copyrights);
        }
        return this;
    }

    String toTxt() {
        StringBuffer buffer = new StringBuffer();
        buffer.append((String.format("__%s__\r\n", "".equals(version) ? name : name + " " + version)));
        if (website != null && "".equals(website) == false) {
            buffer.append(String.format(" * %s\r\n", website));
        }
        if (!licenseAliases.isEmpty()) {
            if (licenseAliases.size() == 1) {
                buffer.append(String.format(" * License: %s\r\n", licenseAliases.iterator().next()));
            } else {
                buffer.append(" * Licenses:\r\n");
                licenseAliases.forEach(alias -> buffer.append(String.format("   * %s\r\n", alias)));
            }
        }
        if (!copyrights.isEmpty()) {
            buffer.append(" * Copyright:\r\n");
            copyrights.forEach(cp -> buffer.append(String.format("   * %s\r\n", cp)));
        } else {
            buffer.append(" * Copyright: (no copyright)\r\n");
        }
        return buffer.toString();
    }

    public void validate() {
        if (null == name || name.isBlank()) {
            throw new IllegalArgumentException("Got empty name of the library");
        }
        if (null == version || version.isBlank()) {
            throw new IllegalArgumentException("Got empty version of the library");
        }
        if (null == website || website.isBlank()) {
            log.warn("Got empty website for library with name '{}' and version {}", name, version);
        } else {
            if (!website.startsWith("http")) {
                log.warn("website for library with name '{}' and version {} does not start with http: {}", name,
                        version, website);
            }
        }
        if (licenseAliases.isEmpty()) {
            throw new IllegalArgumentException(
                    String.format("Got empty licenseAliases of the with name '%s' and version %s", name, version));
        }
        if (copyrights.isEmpty()) {
            log.warn("Got empty copyrights for library with name '{}', version {}, license: {} and website {}",
                    name,
                    version,
                    (licenseAliases.isEmpty() ? "none" : licenseAliases.iterator().next()),
                    website);
        }
    }

    @Override
    public String toString() {
        return "LibraryNotice{" +
                "name='" + name + '\'' +
                ", version='" + version + '\'' +
                ", website='" + website + '\'' +
                ", licenseAliases=" + licenseAliases +
                ", copyrights=" + copyrights +
                '}';
    }
}
