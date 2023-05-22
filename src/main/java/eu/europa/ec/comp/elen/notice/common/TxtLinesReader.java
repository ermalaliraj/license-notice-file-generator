package eu.europa.ec.comp.elen.notice.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Allow reading all lines in a TXT like file.
 */
public class TxtLinesReader {
    private final Reader reader;

    public TxtLinesReader(Reader reader) {
        this.reader = reader;
    }

    public Collection<String> readAllLines() throws IOException {
        return readAllLines(reader);
    }

    public static Collection<String> readAllLines(Reader reader) throws IOException {
        try (BufferedReader br = asBufferedReader(reader)) {
            List<String> result = new ArrayList<>();
            for (; ; ) {
                String line = br.readLine();
                if (line == null)
                    break;
                result.add(line);
            }
            return result;
        }
    }

    private static BufferedReader asBufferedReader(Reader reader) {
        if (reader instanceof BufferedReader) {
            return (BufferedReader)reader;
        }
        return new BufferedReader(reader);
    }
}
