package eu.europa.ec.comp.elen.notice.license;

import org.silentsoft.oss.License;

/**
 * License found for projects like:
 * <ul>
 *     <li>Botocss core (com.atlassian.botocss:botocss-core:6.5 - https://bitbucket.org/atlassian/botocss)</li>
 *     <li>ANTLR 3 Runtime (org.antlr:antlr-runtime:3.5.2 - http://www.antlr.org)</li>
 *     <li>ANTLR 3 Tool (org.antlr:antlr:3.5.2 - http://antlr.org/antlr)</li>
 *     <li>StringTemplate 4 (org.antlr:ST4:4.0.8 - http://www.stringtemplate.org)</li>
 * </ul>
 * but is a bit different from the original BSD which according to Wikipedia was having 4 clauses (see https://en.wikipedia.org/wiki/BSD_licenses).
 */
public class BSDJavaLicense extends License {
    public BSDJavaLicense() {
        super("BSD License", BSDJavaLicense.class.getResourceAsStream("/license/BSDJava.txt"));
    }

    @Override
    public String[] getAliases() {
        return new String[] { "BSD licence", "BSD" };
    }
}
