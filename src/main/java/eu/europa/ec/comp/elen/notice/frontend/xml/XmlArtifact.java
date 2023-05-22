package eu.europa.ec.comp.elen.notice.frontend.xml;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.util.List;

public class XmlArtifact {

    private String groupId;
    private List<String> artifactIds;
    private String version;
    private CopyrightsAndLicenseAliases copyrightsAndLicenseAliases;

    public XmlArtifact(String groupId, List<String> artifactIds, String version, CopyrightsAndLicenseAliases copyrightsAndLicenseAliases) {
        this.groupId = groupId;
        this.artifactIds = artifactIds;
        this.version = version;
        this.copyrightsAndLicenseAliases = copyrightsAndLicenseAliases;
    }

    public String getGroupId() {
        return groupId;
    }

    public List<String> getArtifactIds() {
        return artifactIds;
    }

    public String getVersion() {
        return version;
    }

    public CopyrightsAndLicenseAliases getCopyrightsAndLicenseAliases() {
        return copyrightsAndLicenseAliases;
    }

    public boolean equals(final Object other) {
        if (!(other instanceof XmlArtifact))
            return false;
        XmlArtifact castOther = (XmlArtifact) other;
        return new EqualsBuilder()
                .append(groupId, castOther.groupId)
                .append(artifactIds, castOther.artifactIds)
                .append(version, castOther.version)
                .append(copyrightsAndLicenseAliases, castOther.copyrightsAndLicenseAliases)
                .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(groupId)
                .append(artifactIds)
                .append(version)
                .append(copyrightsAndLicenseAliases)
                .toHashCode();
    }

    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString())
                .append("groupId", groupId)
                .append("artifactIds", artifactIds)
                .append("version", version)
                .append("copyrightsAndLicenseAliases", copyrightsAndLicenseAliases)
                .toString();
    }
}
