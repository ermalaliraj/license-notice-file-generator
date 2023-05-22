package eu.europa.ec.comp.elen.notice.license;

import org.silentsoft.oss.License;

public class MIT_X11License extends License {
    public MIT_X11License() {
        super("MIT/X11 License", MIT_X11License.class.getResourceAsStream("/license/MIT_X11.txt"));
    }

    @Override
    public String[] getAliases() {
        return new String[] { "MIT/X11", "MIT/X11 license" };
    }
}
