package eu.europa.ec.comp.elen.notice.license;

import org.silentsoft.oss.License;

public class EUPL1_1License extends License {
    public EUPL1_1License() {
        super("EUROPEAN UNION PUBLIC LICENCE v. 1.1", EUPL1_1License.class.getResourceAsStream("/license/EUPL-1.1.txt"));
    }

    @Override
    public String[] getAliases() {
        return new String[] { "EUPL-1.1", "EUROPEAN UNION PUBLIC LICENCE v. 1.1" };
    }
}
