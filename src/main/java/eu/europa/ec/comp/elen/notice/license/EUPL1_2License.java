package eu.europa.ec.comp.elen.notice.license;

import org.silentsoft.oss.License;

public class EUPL1_2License extends License {
    public EUPL1_2License() {
        super("EUROPEAN UNION PUBLIC LICENCE v. 1.2", EUPL1_2License.class.getResourceAsStream("/license/EUPL-1.2.txt"));
    }

    @Override
    public String[] getAliases() {
        return new String[] { "EUPL-1.2", "EUROPEAN UNION PUBLIC LICENCE v. 1.2" };
    }
}
