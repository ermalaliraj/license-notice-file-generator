package eu.europa.ec.comp.elen.notice.common;

public class NpmJsDependencyNameTxtConverter {
    /**
     * Convert texts like 'upath@1.2.0' and '@foliojs-fork/linebreak@1.1.1' to {@link NpmJsDependencyName} instances.
     * @param text The text to parse.
     * @return The created instance of {@link NpmJsDependencyName}.
     */
    public NpmJsDependencyName convert(String text) {
        int startVersionIndex = text.lastIndexOf("@");
        String version = text.substring(startVersionIndex+1);
        final String nameWithoutVersion = text.substring(0, startVersionIndex);
        String[] names = nameWithoutVersion.split("/");
        if (names.length == 1) {
            return new NpmJsDependencyName("-", names[0], version);
        }
        return new NpmJsDependencyName(names[0], names[1], version);
    }

}
