package eu.europa.ec.comp.elen.notice.license;

import org.silentsoft.oss.License;

public class CDDL1_0License extends License {
    public CDDL1_0License() {
        super("COMMON DEVELOPMENT AND DISTRIBUTION LICENSE (CDDL) Version 1.0", CDDL1_0License.class.getResourceAsStream("/license/CDDL-1.0.txt"));
    }

    @Override
    public String[] getAliases() {
        return new String[] { "CDDL-1.0", "Commons Development and Distribution License, Version 1.0"};
    }
}
