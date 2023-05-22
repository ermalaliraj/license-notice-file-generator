package eu.europa.ec.comp.elen.notice.remote;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class RemoteNotice {
    private String fullName;
    private String name;
    private String version;
    private String website;
    private String license;
    private List<String> copyrights = Collections.emptyList();
    private String text;
    private boolean defined;

    public boolean isDefined() {
        return defined;
    }

    public void setDefined(boolean defined) {
        this.defined = defined;
    }

    public String getFullName() {
        return fullName;
    }

    void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    void setVersion(String version) {
        this.version = version;
    }

    public String getWebsite() {
        return website;
    }

    public boolean hasWebsite() {
        return null != website && !website.isBlank();
    }

    void setWebsite(String website) {
        this.website = website;
    }

    public Optional<String> getLicense() {
        return Optional.ofNullable(license);
    }

    void setLicense(String license) {
        this.license = license;
    }

    public List<String> getCopyrights() {
        return copyrights;
    }

    void setCopyrights(List<String> copyrights) {
        this.copyrights = copyrights;
    }

    public boolean hasCopyrights() {
        return !copyrights.isEmpty();
    }

    public String getText() {
        return text;
    }

    void setText(String text) {
        this.text = text;
    }

    public boolean hasValidText() {
        // for some libraries ClearlyDefined will return same text for license and text;
        // e.g. 'com.github.librepdf/openpdf/1.3.11' received license and
        // text are both: 'Apache-2.0 AND LGPL-2.0-only AND LGPL-3.0 AND MPL-1.1'
        return null != text && !text.equalsIgnoreCase(license);
    }

    @Override
    public String toString() {
        return "Notice{" +
                "defined=" + defined +
                ", fullName='" + fullName + '\'' +
                ", name='" + name + '\'' +
                ", version='" + version + '\'' +
                ", website='" + website + '\'' +
                ", license='" + license + '\'' +
                ", copyrights=" + copyrights +
//                ", text='" + text + '\'' +
                '}';
    }
}
