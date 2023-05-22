package eu.europa.ec.comp.elen.notice.backend.xml;

import java.util.List;

public class Dependency {

    private MavenPackage mvnPackage;
    private String url;
    private String licenseAlias;
    private List<String> copyrights;

    public Dependency(MavenPackage mvnPackage, String url, String licenseAlias, List<String> copyrights) {
        this.mvnPackage = mvnPackage;
        this.url = url;
        this.licenseAlias = licenseAlias;
        this.copyrights = copyrights;
    }

    public MavenPackage getMvnPackage() {
        return mvnPackage;
    }

    public String getUrl() {
        return url;
    }

    public String getLicenseAlias() {
        return licenseAlias;
    }

    public List<String> getCopyrights() {
        return copyrights;
    }
}
