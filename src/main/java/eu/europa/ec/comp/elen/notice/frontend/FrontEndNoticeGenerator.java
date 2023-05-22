package eu.europa.ec.comp.elen.notice.frontend;

import eu.europa.ec.comp.elen.notice.common.PathRetriever;
import eu.europa.ec.comp.elen.notice.common.Product;
import eu.europa.ec.comp.elen.notice.frontend.csv.CsvLibrary;
import eu.europa.ec.comp.elen.notice.frontend.csv.CsvToCsvLibrariesConverter;
import eu.europa.ec.comp.elen.notice.frontend.xml.CopyrightsResult;
import eu.europa.ec.comp.elen.notice.frontend.xml.NpmJsXmlCopyrightsMapping;
import eu.europa.ec.comp.elen.notice.generator.LibraryNoticeV3;
import eu.europa.ec.comp.elen.notice.generator.NoticeFileGeneratorV3;
import eu.europa.ec.comp.elen.notice.license.EUPL1_1License;
import eu.europa.ec.comp.elen.notice.license.EUPLv1_2Content;
import eu.europa.ec.comp.elen.notice.license.MIT_X11License;
import eu.europa.ec.comp.elen.notice.remote.NoticeService;
import eu.europa.ec.comp.elen.notice.remote.RemoteNotice;
import org.silentsoft.oss.LicenseDictionary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Generate notice file from a CSV file with license information, following format:
 * <pre>
 * "module name","license","repository"
 * "@aduh95/viz.js@3.4.0","MIT","https://github.com/aduh95/viz.js"
 * "@angular-devkit/architect@0.13.10","MIT","https://github.com/angular/angular-cli"
 * "@angular-devkit/architect@0.13.9","MIT","https://github.com/angular/angular-cli"
 * "@angular-devkit/build-angular@0.13.10","MIT","https://github.com/angular/angular-cli"
 * </pre>
 * where first line contains the name of the columns.
 */
public class FrontEndNoticeGenerator {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final Path csvFile;
    private final Product product;

    private final NpmJsXmlCopyrightsMapping xmlCopyrightsMapping;

    public FrontEndNoticeGenerator(Product product, Path csvFile, NpmJsXmlCopyrightsMapping xmlCopyrightsMapping) {
        this.xmlCopyrightsMapping = xmlCopyrightsMapping;
        Objects.requireNonNull(product);
        Objects.requireNonNull(csvFile);
        this.product = product;
        this.csvFile = csvFile;

        this.registerExtraLicenses();
    }

    void generateNotice(OutputStream os) throws IOException, InterruptedException {
        final Collection<CsvLibrary> libraries = keepOneLibraryOccurrence(getLibrariesFromCsv());
        final List<String> namespaceAndNames = libraries.stream()
                                                        .map(CsvLibrary::getFullName)
//                                                        .toList();
                                                        .collect(Collectors.toList());

        final List<RemoteNotice> remoteNotices = new NoticeService().retrieveNotices(namespaceAndNames,
                NoticeService.NoticeProvider.NPMJS);

        List<LibraryNoticeV3> notices = merge(libraries, remoteNotices);

        PrintStream ps = new PrintStream(os, true);
        final NoticeFileGeneratorV3.NoticeFileBuilder noticeBuilder = NoticeFileGeneratorV3.newInstance(
                product.getName(), product.getOwner());
        noticeBuilder.addText("Version: " + product.getVersion());
        noticeBuilder.addText(product.getLicenseContent());
        noticeBuilder.addText(
                "This product includes dynamically linked software developed by third parties which is provided under their respective licences:");

        notices.forEach(noticeBuilder::addLibrary);

        String markdown = noticeBuilder.generate();
        ps.print(markdown);
    }

    private List<LibraryNoticeV3> merge(Collection<CsvLibrary> libraries, List<RemoteNotice> remoteNotices) {
        final Map<String, RemoteNotice> fullNameToRemoteNotice = remoteNotices
                .stream()
                .collect(Collectors.toMap(RemoteNotice::getFullName,
                        Function.identity()));
        return libraries
                .stream()
                .map(library -> toLibraryNotice(library, fullNameToRemoteNotice.get(library.getFullName())))
//                .toList();
                .collect(Collectors.toList());
    }

    private LibraryNoticeV3 toLibraryNotice(CsvLibrary library, RemoteNotice remoteNotice) {
        var libNotice = new LibraryNoticeV3()
                .withName(library.getCleanName())
                .withVersion(library.getVersion())
                .withWebsite(library.getUrl())
                .withLicenseAlias(library.getLicences());

        final Optional<CopyrightsResult> copyrightMatch = xmlCopyrightsMapping.match(library.getNpmJsDependencyName());

        if (copyrightMatch.isPresent()) {
            final CopyrightsResult copyrightsResult = copyrightMatch.get();
            libNotice.withCopyrights(copyrightsResult.getCopyrights());
            if (!copyrightsResult.getLicenseAliases().isEmpty()) {
                libNotice.withLicenseAlias(copyrightsResult.getLicenseAliases());
            }
        } else if (null != remoteNotice) {
            // use mainly copyright from RemoteNotice
            if (remoteNotice.hasCopyrights()) {
                libNotice.withCopyrights(remoteNotice.getCopyrights());
            }
            if (remoteNotice.hasWebsite() && !remoteNotice.getWebsite().equals(library.getUrl())) {
                log.warn("Got different website for {}. From XML: {}, from remote: {}",
                        library.getCleanName(), library.getUrl(), remoteNotice.getWebsite());
            }
        }

        libNotice.validate();
        return libNotice;
    }

    private Collection<CsvLibrary> getLibrariesFromCsv() throws IOException {
        return new CsvToCsvLibrariesConverter()
                .convertCsv(Files.newBufferedReader(this.csvFile));
    }

    private Collection<CsvLibrary> keepOneLibraryOccurrence(Collection<CsvLibrary> libraries) {
        final Map<String, List<CsvLibrary>> librariesByNamespace = libraries.stream()
                                                                            .collect(Collectors.groupingBy(
                                                                                    CsvLibrary::getCleanName));
        if (libraries.size() != librariesByNamespace.size()) {
            final List<CsvLibrary> uniqueLibraries = librariesByNamespace
                    .entrySet()
                    .stream()
                    .map(this::selectCsvLibrary)
                    .sorted(Comparator.comparing(CsvLibrary::getFullName))
//                    .toList();
                    .collect(Collectors.toList());
            log.info("Kept {} unique libraries out of {}", uniqueLibraries.size(), libraries.size());
            return uniqueLibraries;
        }
        return libraries;
    }

    private CsvLibrary selectCsvLibrary(Map.Entry<String, List<CsvLibrary>> entry) {
        final List<CsvLibrary> libs = entry.getValue();
        if (libs.size() == 1) {
            return libs.get(0);
        }
        // select the Library having the highest version
        final Comparator<CsvLibrary> cmpByVersion = (o1, o2) -> o1.getVersion().compareToIgnoreCase(o2.getVersion());
        final CsvLibrary max = Collections.max(libs, cmpByVersion);
        log.info("Kept lib with version {} from {} libs for dependency {}", max.getVersion(), libs.size(),
                max.getCleanName());
        return max;
    }

    private void registerExtraLicenses() {
        LicenseDictionary.put(new EUPL1_1License(), new MIT_X11License());
    }

    public static void main(String[] args)
            throws URISyntaxException, IOException, InterruptedException, ParserConfigurationException, SAXException {
        final Path xmlCopyrights = new PathRetriever().fromClasspath("/npmjs-copyrights-lookup.xml");
        final NpmJsXmlCopyrightsMapping xmlCopyrightsMapping = new NpmJsXmlCopyrightsMapping(xmlCopyrights);

        final Path trustedCsvFile = Path.of("etc/trusted-frontend-third-party-licenses.csv");
        final Product trustedApp = new Product("eLeniency Trusted Application FrontEnd", "2022 European Union", "1.0",
                EUPLv1_2Content.content());
        FrontEndNoticeGenerator trustedAppGenerator = new FrontEndNoticeGenerator(trustedApp, trustedCsvFile,
                xmlCopyrightsMapping);
        try (PrintStream ps = new PrintStream("NOTICE_Trusted_FE.md")) {
            trustedAppGenerator.generateNotice(ps);
        }
    }
}
