package eu.europa.ec.comp.elen.notice.common;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class NpmJsDependencyName {
    private String namespace;
    private String name;
    private String version;

    NpmJsDependencyName(String namespace, String name, String version) {
        this.namespace = namespace;
        this.name = name;
        this.version = version;
    }

    public String namespaceAndName() {
        return String.format("%s/%s", namespace, name);
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
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

    /**
     * @return <tt>true</tt> if namespace is not '-', <tt>false</tt> otherwise.
     */
    public boolean hasValidNamespace() {
        return !"-".equals(namespace);
    }

    public boolean equals(final Object other) {
        if (!(other instanceof NpmJsDependencyName))
            return false;
        NpmJsDependencyName castOther = (NpmJsDependencyName) other;
        return new EqualsBuilder()
                .append(namespace, castOther.namespace)
                .append(name, castOther.name)
                .append(version, castOther.version).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(namespace)
                .append(name)
                .append(version).toHashCode();
    }

    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString())
                .append("namespace", namespace)
                .append("name", name)
                .append("version", version).toString();
    }

}
