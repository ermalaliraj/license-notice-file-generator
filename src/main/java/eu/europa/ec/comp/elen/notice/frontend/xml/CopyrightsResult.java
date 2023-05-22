package eu.europa.ec.comp.elen.notice.frontend.xml;

import eu.europa.ec.comp.elen.notice.backend.xml.MatchLevel;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.util.List;

/**
 * Data about license associated to one library/ component in the XML file. One library can have multiple licenses.
 */
public class CopyrightsResult {
    private List<String> copyrights;
    private List<String> licenseAliases;
    private MatchLevel matchLevel;

    public CopyrightsResult(List<String> copyrights, List<String> licenseAliases, MatchLevel matchLevel) {
        this.copyrights = copyrights;
        this.licenseAliases = licenseAliases;
        this.matchLevel = matchLevel;
    }

    public List<String> getCopyrights() {
        return copyrights;
    }

    public List<String> getLicenseAliases() {
        return licenseAliases;
    }

    public MatchLevel getMatchLevel() {
        return matchLevel;
    }

    public boolean equals(final Object other) {
        if (!(other instanceof CopyrightsResult))
            return false;
        CopyrightsResult castOther = (CopyrightsResult) other;
        return new EqualsBuilder()
                .append(copyrights, castOther.copyrights)
                .append(licenseAliases, castOther.licenseAliases)
                .append(matchLevel, castOther.matchLevel)
                .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(copyrights)
                .append(licenseAliases)
                .append(matchLevel)
                .toHashCode();
    }

    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString())
                .append("copyrights", copyrights)
                .append("licenseAliases", licenseAliases)
                .append("matchLevel", matchLevel)
                .toString();
    }
}
