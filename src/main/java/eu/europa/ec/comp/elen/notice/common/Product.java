package eu.europa.ec.comp.elen.notice.common;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class Product {

    private String name;
    private String owner;
    private String version;
    private String licenseContent;

    public Product(String name, String owner, String version, String licenseContent) {
        this.name = name;
        this.owner = owner;
        this.version = version;
        this.licenseContent = licenseContent;
    }

    public String getName() {
        return name;
    }

    public String getOwner() {
        return owner;
    }

    public String getVersion() {
        return version;
    }

    public String getLicenseContent() {
        return licenseContent;
    }

    public boolean equals(final Object other) {
        if (!(other instanceof Product))
            return false;
        Product castOther = (Product) other;
        return new EqualsBuilder()
                .append(name, castOther.name)
                .append(owner, castOther.owner)
                .append(version, castOther.version)
                .append(licenseContent, castOther.licenseContent)
                .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(name)
                .append(owner)
                .append(version)
                .append(licenseContent)
                .toHashCode();
    }

    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString())
                .append("name", name)
                .append("owner", owner)
                .append("version", version)
                .append("licenseContent", licenseContent)
                .toString();
    }

}
