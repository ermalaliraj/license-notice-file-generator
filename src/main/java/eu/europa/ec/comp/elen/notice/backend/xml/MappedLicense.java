package eu.europa.ec.comp.elen.notice.backend.xml;

import java.text.Format;
import java.util.List;

public class MappedLicense {
    private String groupId;
    private String artifactId;
    private List<String> licenseAliases;

    MappedLicense(String groupId, String artifactId, List<String> licenseAliases) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.licenseAliases = licenseAliases;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public List<String> getLicenseAliases() {
        return licenseAliases;
    }

    public String completeName() {
        return completeName(groupId, artifactId);
    }

    static String completeName(String groupId, String artifactId) {
        return  String.format("%s:%s", groupId, artifactId);
    }
}