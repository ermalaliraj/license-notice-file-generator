package eu.europa.ec.comp.elen.notice.frontend.csv;

import eu.europa.ec.comp.elen.notice.common.TxtLinesReader;

import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

public class CsvToCsvLibrariesConverter {
    public Collection<CsvLibrary> convertCsv(Reader reader) throws IOException {
        Objects.requireNonNull(reader);

        var lineConverter = new CsvLineToCsvLibraryConverter();
        return readAllLines(reader)
                .stream()
                .skip(1)
                .filter(line -> line.trim().length() > 0)
                .map(lineConverter::convert)
//                .toList();
                .collect(Collectors.toList());
    }

    private Collection<String> readAllLines(Reader reader) throws IOException {
        return new TxtLinesReader(reader).readAllLines();
    }
}
