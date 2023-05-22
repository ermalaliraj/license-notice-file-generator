package eu.europa.ec.comp.elen.notice.backend.xml;

import eu.europa.ec.comp.elen.notice.frontend.csv.CsvLibrary;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.util.List;

/**
 * Data about a library/ component as specified in the XML file.
 */
public class XmlLibrary {

    private String groupId;
    private String artifactId;
    private String version;
    private List<XmlLicense> licenses;

    XmlLibrary(String groupId, String artifactId, String version, List<XmlLicense> licenses) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.licenses = licenses;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getVersion() {
        return version;
    }

    public List<XmlLicense> getLicenses() {
        return licenses;
    }

    public boolean equals(final Object other) {
        if (!(other instanceof CsvLibrary))
            return false;
        XmlLibrary castOther = (XmlLibrary) other;
        return new EqualsBuilder()
                .append(groupId, castOther.groupId)
                .append(artifactId, castOther.artifactId)
                .append(version, castOther.version)

                .append(licenses, castOther.licenses)
                .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(groupId)
                .append(artifactId)
                .append(version)
                .append(licenses)
                .toHashCode();
    }

    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString())
                .append(groupId, groupId)
                .append("artifactId", artifactId)
                .append("version", version)
                .append("licenses", licenses)
                .toString();
    }
}
