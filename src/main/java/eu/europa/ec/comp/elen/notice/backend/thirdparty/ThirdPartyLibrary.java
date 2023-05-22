package eu.europa.ec.comp.elen.notice.backend.thirdparty;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.List;

/**
 * Library data extracted from a THIRD-PARYTY.txt file.
 */
public class ThirdPartyLibrary {
    private String name;
    private String projectUrl;
    private String groupId;
    private String artifactId;
    private String version;
    private List<String> licenseAliases;

    void setName(String name) {
        this.name = name;
    }

    void setProjectUrl(String projectUrl) {
        this.projectUrl = projectUrl;
    }

    void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    void setVersion(String version) {
        this.version = version;
    }

    void setLicenseAliases(List<String> licenseAliases) {
        this.licenseAliases = licenseAliases;
    }

    public String getName() {
        return name;
    }

    public String getProjectUrl() {
        return projectUrl;
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

    public List<String> getLicenseAliases() {
        return licenseAliases;
    }

    /**
     * @return A String in format groupId:artifactId
     */
    public String groupIdAndArtifactId() {
        return String.format("%s:%s", groupId, artifactId);
    }

    /**
     * @return A String in format groupId:artifactId:version
     */
    public String fullDependencyPath() {
        return String.format("%s:%s:%s", groupId, artifactId, version);
    }

    public boolean equals(final Object other) {
        if (!(other instanceof ThirdPartyLibrary))
            return false;
        ThirdPartyLibrary castOther = (ThirdPartyLibrary) other;
        return new EqualsBuilder()
                .append(groupId, castOther.groupId)
                .append(artifactId, castOther.artifactId)
                .append(version, castOther.version).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(groupId)
                .append(artifactId)
                .append(version).toHashCode();
    }

    @Override
    public String toString() {
        return "ThirdPartyLibrary{" +
                "name='" + name + '\'' +
                ", projectUrl='" + projectUrl + '\'' +
                ", groupId='" + groupId + '\'' +
                ", artifactId='" + artifactId + '\'' +
                ", version='" + version + '\'' +
                ", licenseAliases=" + licenseAliases +
                '}';
    }
}
