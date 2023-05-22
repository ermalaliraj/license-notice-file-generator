package eu.europa.ec.comp.elen.notice.remote;

import java.util.List;

class NoticesResponse {

    private Content content;
    private Summary summary;

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public Summary getSummary() {
        return summary;
    }

    public void setSummary(Summary summary) {
        this.summary = summary;
    }

    @Override
    public String toString() {
        return "Notices{" +
                "content=" + content +
                ", summary=" + summary +
                '}';
    }

    public class Content {
        private List<Package> packages;

        public List<Package> getPackages() {
            return packages;
        }

        public void setPackages(List<Package> packages) {
            this.packages = packages;
        }

        @Override
        public String toString() {
            return "Content{" +
                    "packages=" + packages +
                    '}';
        }
    }

    public static class Package {
        private String uuid;
        private String name;
        private String version;
        private String website;
        private String license;
        private String text;
        private List<String> copyrights;

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getWebsite() {
            return website;
        }

        public void setWebsite(String website) {
            this.website = website;
        }

        public String getLicense() {
            return license;
        }

        public void setLicense(String license) {
            this.license = license;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public List<String> getCopyrights() {
            return copyrights;
        }

        public void setCopyrights(List<String> copyrights) {
            this.copyrights = copyrights;
        }

        @Override
        public String toString() {
            return "Package{" +
                    "uuid='" + uuid + '\'' +
                    ", name='" + name + '\'' +
                    ", version='" + version + '\'' +
                    ", website='" + website + '\'' +
                    ", license='" + license + '\'' +
                    ", text='" + fragment(text) + '\'' +
                    ", copyrights=" + copyrights +
                    '}';
        }

        private String fragment(String text) {
            if (null == text || text.isBlank()) {
                return text;
            }
            if (text.length() > 100) {
                return text.substring(0, 99) + "...";
            }
            return text;
        }
    }

    public static class Summary {
        private Long total;
        private Warnings warnings;

        public Long getTotal() {
            return total;
        }

        public void setTotal(Long total) {
            this.total = total;
        }

        public Warnings getWarnings() {
            return warnings;
        }

        public void setWarnings(Warnings warnings) {
            this.warnings = warnings;
        }

        @Override
        public String toString() {
            return "Summary{" +
                    "total=" + total +
                    ", warnings=" + warnings +
                    '}';
        }
    }

    public static class Warnings {
        public List<String> noDefinition;
        public List<String> noLicense;
        public List<String> noCopyright;

        public List<String> getNoDefinition() {
            return noDefinition;
        }

        public void setNoDefinition(List<String> noDefinition) {
            this.noDefinition = noDefinition;
        }

        public List<String> getNoLicense() {
            return noLicense;
        }

        public void setNoLicense(List<String> noLicense) {
            this.noLicense = noLicense;
        }

        public List<String> getNoCopyright() {
            return noCopyright;
        }

        public void setNoCopyright(List<String> noCopyright) {
            this.noCopyright = noCopyright;
        }

        @Override
        public String toString() {
            return "Warnings{" +
                    "noDefinition=" + noDefinition +
                    ", noLicense=" + noLicense +
                    ", noCopyright=" + noCopyright +
                    '}';
        }
    }
}