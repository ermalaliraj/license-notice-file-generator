package eu.europa.ec.comp.elen.notice.frontend.xml;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.util.List;

public class CopyrightsAndLicenseAliases {
    private List<String> copyrights;
    private List<String> licenseAliases;

    public CopyrightsAndLicenseAliases(List<String> copyrights, List<String> licenseAliases) {
        this.copyrights = copyrights;
        this.licenseAliases = licenseAliases;
    }

    public List<String> getCopyrights() {
        return copyrights;
    }

    public List<String> getLicenseAliases() {
        return licenseAliases;
    }

    public boolean equals(final Object other) {
        if (!(other instanceof CopyrightsAndLicenseAliases))
            return false;
        CopyrightsAndLicenseAliases castOther = (CopyrightsAndLicenseAliases) other;
        return new EqualsBuilder()
                .append(copyrights, castOther.copyrights)
                .append(licenseAliases, castOther.licenseAliases)
                .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(copyrights)
                .append(licenseAliases)
                .toHashCode();
    }

    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString())
                .append("copyrights", copyrights)
                .append("licenseAliases", licenseAliases)
                .toString();
    }
}
