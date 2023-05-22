package eu.europa.ec.comp.elen.notice.backend.thirdparty;

import eu.europa.ec.comp.elen.notice.common.TxtLinesReader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Converts text from generated <tt>THIRD-PARTY.txt</tt> files to a list of <tt>ThirdPartyLibrary</tt> instances.
 */
public class ThirdPartyConverter {
    public List<ThirdPartyLibrary> convertTxt(Path txtFile) throws IOException {
        return readLines(txtFile)
                .map(this::convertLine)
                .distinct()
//                .toList();
                .collect(Collectors.toList());
    }

    private static Stream<String> readLines(Path txtFile) throws IOException {
        return new TxtLinesReader(Files.newBufferedReader(txtFile))
                .readAllLines()
                .stream()
                // skip empty lines
                .filter(line -> !line.isBlank())
                .map(String::trim)
                // ignore lines that do not start with ( and end in )
                .filter(line -> line.startsWith("(") && line.endsWith(")"));
    }

    private ThirdPartyLibrary convertLine(String line) {
        // Process a line like:
        // (ASF 2.0) Code Generation Library (cglib:cglib:2.2.2 - http://cglib.sourceforge.net/)
        final int index1 = line.indexOf(')');
        final int index2 = line.indexOf('(', index1 + 1);
        final String[] licenseAliases = extractLicenses(line.substring(1, index1));
        final String name = line.substring(index1 + 1, index2).trim();
        final String[] artifactAndUrl = extractArtifactAndUrl(line.substring(index2 + 1, line.length() - 1));
        final String[] artifactData = extractArtifactData(artifactAndUrl[0]);
        final String url = artifactAndUrl[1];

        final ThirdPartyLibrary library = new ThirdPartyLibrary();
        library.setName(name);
        library.setGroupId(artifactData[0]);
        library.setArtifactId(artifactData[1]);
        library.setVersion(artifactData[2]);
        library.setProjectUrl(url);
        library.setLicenseAliases(Arrays.asList(licenseAliases));

        return library;
    }

    private static String[] extractArtifactData(String text) {
        // text should be like: cglib:cglib:2.2.2
        final String[] data = text.split(":");
        if (data.length != 3) {
            throw new IllegalStateException("Could not extract groupId, artifactId and version from: " + text);
        }
        return data;
    }

    private static String[] extractArtifactAndUrl(String text) {
        // text should be like: cglib:cglib:2.2.2 - http://cglib.sourceforge.net/
        final String[] split = text.split(" - ");
        if (split.length != 2) {
            throw new IllegalStateException("Could not extract artifact info and url from: " + text);
        }
        return split;
    }

    private static String[] extractLicenses(String text) {
        if (text.contains(" OR ")) {
            return text.split(" OR ");
        }
        if (text.contains(" AND ")) {
            return text.split(" AND ");
        }
        var result = new String[1];
        result[0] = text;
        return result;
    }
}
