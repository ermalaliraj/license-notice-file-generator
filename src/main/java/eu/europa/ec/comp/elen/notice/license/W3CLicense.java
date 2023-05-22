package eu.europa.ec.comp.elen.notice.license;

import org.silentsoft.oss.License;

public class W3CLicense extends License {
    public W3CLicense() {
        super("The W3C License", W3CLicense.class.getResourceAsStream("/license/W3C.txt"));
    }

    @Override
    public String[] getAliases() {
        return new String[] { "The W3C License" };
    }
}
