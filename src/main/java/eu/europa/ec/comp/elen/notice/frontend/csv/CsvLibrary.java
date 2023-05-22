package eu.europa.ec.comp.elen.notice.frontend.csv;

import eu.europa.ec.comp.elen.notice.common.NpmJsDependencyName;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.util.List;

public class CsvLibrary {
    private NpmJsDependencyName npmJsDependencyName;
    private String url;
    private List<String> licences;

    public CsvLibrary(NpmJsDependencyName npmJsDependencyName, String url, List<String> licences) {
        this.npmJsDependencyName = npmJsDependencyName;
        this.url = url;
        this.licences = licences;
    }

    public String getVersion() {
        return npmJsDependencyName.getVersion();
    }

    public String getCleanName() {
        if (npmJsDependencyName.hasValidNamespace()) {
            return npmJsDependencyName.namespaceAndName();
        }
        return npmJsDependencyName.getName();
    }

    public NpmJsDependencyName getNpmJsDependencyName() {
        return npmJsDependencyName;
    }

    public String getUrl() {
        return url;
    }

    public List<String> getLicences() {
        return licences;
    }

    /**
     * @return a String in format 'namespace/name@version', if namespace is not <tt>-</tt>, otherwise a String in format '/name@version'.
     */
    public String getFullName() {
        if (npmJsDependencyName.hasValidNamespace()) {
            return String.format("%s/%s@%s", npmJsDependencyName.getNamespace(), npmJsDependencyName.getName(),
                    npmJsDependencyName.getVersion());
        }
        return String.format("%s@%s", npmJsDependencyName.getName(), npmJsDependencyName.getVersion());
    }


    public boolean equals(final Object other) {
        if (!(other instanceof CsvLibrary))
            return false;
        CsvLibrary castOther = (CsvLibrary) other;
        return new EqualsBuilder()
                .append(npmJsDependencyName, castOther.npmJsDependencyName)
                .append(url, castOther.url)
                .append(licences, castOther.licences)
                .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(npmJsDependencyName)
                .append(url)
                .append(licences)
                .toHashCode();
    }

    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString())
                .append("npmJsDependencyName", npmJsDependencyName)
                .append("url", url)
                .append("licences", licences)
                .toString();
    }
}
