package eu.europa.ec.comp.elen.notice.license;

import org.silentsoft.oss.License;

public class SAXLicense extends License {
    public SAXLicense() {
        super("The SAX License", SAXLicense.class.getResourceAsStream("/license/SAX.txt"));
    }

    @Override
    public String[] getAliases() {
        return new String[] { "The SAX License" };
    }
}
