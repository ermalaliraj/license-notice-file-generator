package eu.europa.ec.comp.elen.notice.frontend.xml;

import eu.europa.ec.comp.elen.notice.backend.xml.Dependency;
import eu.europa.ec.comp.elen.notice.backend.xml.DependencyText;
import eu.europa.ec.comp.elen.notice.backend.xml.MavenPackage;
import eu.europa.ec.comp.elen.notice.common.TxtLinesReader;
import eu.europa.ec.comp.elen.notice.common.xml.GenericConversionError;
import eu.europa.ec.comp.elen.notice.common.xml.XmlDocumentPersister;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Parses the dependencies from a previously known NOTICE.md file for Npm projects and creates the associated
 * copyrights-lookup.xml file.
 * Example of content to be parsed:
 * <pre>
 * __@yarnpkg/lockfile 1.1.0__
 *  * https://github.com/yarnpkg/yarn/blob/master/packages/lockfile
 *  * License: BSD-2-Clause
 *  * Copyright:
 *    * Copyright (c) 2013-present, Facebook, Inc.
 *    * copyright (c) 2018 Denis Pushkarev
 *    * Copyright Joyent, Inc. and other Node contributors
 *
 * __JSONStream 1.3.5__
 *  * https://github.com/dominictarr/JSONStream
 *  * Licenses:
 *    * Apache-2.0
 *    * MIT
 *  * Copyright:
 *    * Copyright (c) 2011 Dominic Tarr
 * </pre>
 * <p>
 * Any non-dependency header and footer of the file (including licenses) has to be removed.
 */
public class NpmJsNoticeToXmlCopyrights extends TxtLinesReader {

    public NpmJsNoticeToXmlCopyrights(Reader reader) {
        super(reader);
    }

    public void convertNoticeToXml(OutputStream os)
            throws IOException, ParserConfigurationException, TransformerException {
        List<Dependency> dependencies = convertNotices();
        XmlWriter writer = new XmlWriter();
        dependencies.forEach(writer::addDependency);
        writer.writeXml(os);
    }

    private List<Dependency> convertNotices() throws IOException {
        final String content = readContent();
        final List<Dependency> dependencies = new ArrayList<>();
        Optional<DependencyText> optionalDependencyText = nextDependency(content, 0);
        while(optionalDependencyText.isPresent()) {
            final DependencyText dependencyText = optionalDependencyText.get();
            Dependency dependency = convertText(dependencyText.getText());
            dependencies.add(dependency);

            optionalDependencyText = nextDependency(content, dependencyText.getLastIndex());
        }
        return dependencies;
    }

    private Dependency convertText(String dependencyText) {
//          Text is something like:
//__@yarnpkg/lockfile 1.1.0__
// * https://github.com/yarnpkg/yarn/blob/master/packages/lockfile
// * License: BSD-2-Clause
// * Copyright:
//   * Copyright (c) 2013-present, Facebook, Inc.
//   * copyright (c) 2018 Denis Pushkarev
//   * Copyright Joyent, Inc. and other Node contributors

        int startFullName = dependencyText.indexOf("__");
        int endFullName = findEndIndexForFullName(dependencyText, startFullName);
        MavenPackage npmJsPackage = convertName(dependencyText.substring(startFullName+2, endFullName));

        int startUrl = dependencyText.indexOf("*", endFullName + 2);
        int endUrl = dependencyText.indexOf("*", startUrl + 1);
        var url = dependencyText.substring(startUrl+1, endUrl).trim();

        int startLicence = dependencyText.indexOf("* License:", startUrl);
        int endLicense = dependencyText.indexOf("*", startLicence+"* License:".length());
        if (endLicense < 0) {
            throw new GenericConversionError("Cannot find where license ends from position: " + startLicence);
        }
        var license = dependencyText.substring(startLicence+10, endLicense).trim();

        int startCopyrightSection = dependencyText.indexOf("* Copyright", endLicense);
        int startCopyrights = dependencyText.indexOf("*", startCopyrightSection+"* Copyright".length());
        List<String> copyrights = convertCopyrights(dependencyText.substring(startCopyrights));

//        System.out.println("Got package: " + npmJsPackage + ", license: %s".formatted(license) + " url: '%s'".formatted(url)
//                + ", and %d copyrights: %s".formatted(copyrights.size(), copyrights));

        return new Dependency(npmJsPackage, url, license, copyrights);
    }

    private static int findEndIndexForFullName(String text, int startFullName) {
        // should handle cases where full name is like __@types/babel__core 7.1.14__
        final int index = text.indexOf("__", startFullName + 2);
        final char at = text.charAt(index + 2);
        if (Character.isAlphabetic(at)) {
            // full name is like __@types/babel__core 7.1.14__
            return text.indexOf("__", index + 2);
        }
        // full name is like __@schematics/update 0.13.9__
        return index;
    }

    private List<String> convertCopyrights(String text) {
        final String[] strings = text.split("\\* ");
        return Arrays.stream(strings)
                     .map(String::trim)
                     .filter(val -> val.length() > 0)
//                     .toList();
                    .collect(Collectors.toList());
    }

    private MavenPackage convertName(String text) {
        // text is something like: @yarnpkg/lockfile 1.1.0
        // or like: JSONStream 1.3.5
        final String[] nameWithVersion = text.split(" ");
        if (nameWithVersion.length != 2) {
            throw new GenericConversionError(String.format("Invalid dependency name: '%s'", text));
        }
        final String[] groupIdAndArtifactId = nameWithVersion[0].split("/");
        if (groupIdAndArtifactId.length == 2) {
            return new MavenPackage(groupIdAndArtifactId[0], groupIdAndArtifactId[1], nameWithVersion[1]);
        }
        return new MavenPackage("-", nameWithVersion[0], nameWithVersion[1]);
    }

    private String readContent() throws IOException {
        return readAllLines().stream().collect(Collectors.joining("\r\n"));
    }

    private Optional<DependencyText> nextDependency(String content, int fromIndex) {
        if (fromIndex >= 0 ) {
            final int startDependencyIndex = content.indexOf("__", fromIndex);
            if (startDependencyIndex >= fromIndex) {
                final int endDependencyNameIndex = findEndIndexForFullName(content, startDependencyIndex);
                if (endDependencyNameIndex < 0) {
                    throw new GenericConversionError("Invalid content for dependency name starting at position " + startDependencyIndex);
                }
                int nextDependencyIndex = content.indexOf("__", endDependencyNameIndex + 2);
                if (nextDependencyIndex < 0) {
                    // end of text
                    nextDependencyIndex = content.length()-1;
                }
                var dependencyContent = content.substring(startDependencyIndex, nextDependencyIndex);
                return Optional.of(new DependencyText(dependencyContent.trim(), nextDependencyIndex));
            }
        }
        return Optional.empty();
    }
    
    static class XmlWriter {
        private final Document doc;
        private final Element rootElement;

        XmlWriter() throws ParserConfigurationException {
            var docFactory = DocumentBuilderFactory.newInstance();
            var docBuilder = docFactory.newDocumentBuilder();

            // root elements
            this.doc = docBuilder.newDocument();
            this.rootElement = createElement("copyrights-lookup");
            doc.appendChild(rootElement);
        }

        void addDependency(Dependency dependency) {
            final Element artifact = createElement("artifact");
            artifact.appendChild(createElementWithText("groupId", dependency.getMvnPackage().getGroupId()));
            artifact.appendChild(createElementWithText("artifactId", dependency.getMvnPackage().getArtifactId()));
            artifact.appendChild(createElementWithText("version", dependency.getMvnPackage().getVersion()));
            artifact.appendChild(createElementWithText("url", dependency.getUrl()));

            Element license = createElement("license");
            license.appendChild(createElementWithText("name", dependency.getLicenseAlias()));
            artifact.appendChild(license);

            dependency.getCopyrights()
                      .forEach(copyright -> artifact.appendChild(createElementWithText("copyright", copyright)));

            rootElement.appendChild(artifact);
        }

        void writeXml(OutputStream os) throws TransformerException {
            new XmlDocumentPersister().serializeDocument(doc, os);
        }

        private Element createElement(String tagName) {
            return doc.createElement(tagName);
        }

        private Element createElementWithText(String tagName, String text) {
            final Element element = createElement(tagName);
            element.setTextContent(text);
            return element;
        }
    }

    public static void main(String[] args) throws IOException, ParserConfigurationException, TransformerException {
        final Path txtFile = Path.of("NOTICE_Private_FE_no_licenses.md");
        NpmJsNoticeToXmlCopyrights generator = new NpmJsNoticeToXmlCopyrights(Files.newBufferedReader(txtFile));
        generator.convertNoticeToXml(System.out);
    }
}
