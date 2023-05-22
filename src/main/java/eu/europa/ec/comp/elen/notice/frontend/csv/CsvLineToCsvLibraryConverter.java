package eu.europa.ec.comp.elen.notice.frontend.csv;

import eu.europa.ec.comp.elen.notice.common.NpmJsDependencyName;
import eu.europa.ec.comp.elen.notice.common.NpmJsDependencyNameTxtConverter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Converting CSV lines to {@link CsvLibrary} instances.
 * Examples of lines to be converted:
 * <pre>
 * @aduh95/viz.js@3.4.0,MIT,https://github.com/aduh95/viz.js
 * @angular-devkit/architect@0.13.10,MIT,https://github.com/angular/angular-cli
 * </pre>
 */
public class CsvLineToCsvLibraryConverter {

    private final NpmJsDependencyNameTxtConverter nameTxtConverter = new NpmJsDependencyNameTxtConverter();

    public CsvLibrary convert(String line) {
        try {
            return convertInt(line);
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not process line: `" + line + "`", e);
        }
    }

    private CsvLibrary convertInt(String line) {
        // line is like '@aduh95/viz.js@3.4.0,MIT,https://github.com/aduh95/viz.js'
        final List<String> columns = Arrays.stream(line.split(","))
                                           .map(CsvLineToCsvLibraryConverter::removeQuotesIfNeeded)
//                                           .toList();
                                            .collect(Collectors.toList());

        final NpmJsDependencyName npmJsDependencyName = nameTxtConverter.convert(columns.get(0));
        final List<String> licenses = extractLicenses(columns.get(1));
        final String url = columns.get(2);

        return new CsvLibrary(
                npmJsDependencyName,
                url,
                licenses);
    }

    private static String removeQuotesIfNeeded(String column) {
        if (column.startsWith("\"") && column.endsWith("\"")) {
            return column.substring(1, column.length() - 1).trim();
        }
        return column.trim();
    }

    private static List<String> extractLicenses(String text) {
        if (text.startsWith("(") && text.endsWith(")")) {
            text = text.substring(1, text.length() - 1);
        }
        if (text.contains(" OR ")) {
            return Arrays.asList(text.split(" OR "));
        }
        if (text.contains(" AND ")) {
            return Arrays.asList(text.split(" AND "));
        }
        return  Arrays.asList(text);
    }
}
