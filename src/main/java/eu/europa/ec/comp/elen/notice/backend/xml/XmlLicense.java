package eu.europa.ec.comp.elen.notice.backend.xml;

import eu.europa.ec.comp.elen.notice.frontend.csv.CsvLibrary;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Data about license associated to one library/ component in the XML file. One library can have multiple licenses.
 */
public class XmlLicense {
    private String name;
    private String url;
    private String file;

    XmlLicense(String name, String url, String file) {
        this.name = name;
        this.url = url;
        this.file = file;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getFile() {
        return file;
    }

    public boolean equals(final Object other) {
        if (!(other instanceof CsvLibrary))
            return false;
        XmlLicense castOther = (XmlLicense) other;
        return new EqualsBuilder()
                .append(name, castOther.name)
                .append(url, castOther.url)
                .append(file, castOther.file)
                .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(name)
                .append(url)
                .append(file)
                .toHashCode();
    }

    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString())
                .append("name", name)
                .append("url", url)
                .append("file", file)
                .toString();
    }
}
