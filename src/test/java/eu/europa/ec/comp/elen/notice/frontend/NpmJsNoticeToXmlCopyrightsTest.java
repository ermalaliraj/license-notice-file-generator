package eu.europa.ec.comp.elen.notice.frontend;

import eu.europa.ec.comp.elen.notice.frontend.xml.NpmJsNoticeToXmlCopyrights;
import org.junit.jupiter.api.Test;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.StringReader;

class NpmJsNoticeToXmlCopyrightsTest {
    @Test
    void convertTwoDependencies() throws IOException, ParserConfigurationException, TransformerException {
        NpmJsNoticeToXmlCopyrights noticeToXmlCopyrights = new NpmJsNoticeToXmlCopyrights(new StringReader(sampleTwoDependenciesNoticeTxt()));

        noticeToXmlCopyrights.convertNoticeToXml(System.out);
    }

    @Test
    void convertOneDependencyWithSpecialName() throws IOException, ParserConfigurationException, TransformerException {
        NpmJsNoticeToXmlCopyrights noticeToXmlCopyrights = new NpmJsNoticeToXmlCopyrights(new StringReader(sampleOneDependencyNoticeTxt()));

        noticeToXmlCopyrights.convertNoticeToXml(System.out);
    }

    private String sampleOneDependencyNoticeTxt() {
        return "__@types/babel__core 7.1.14__\n" +
                " * https://github.com/DefinitelyTyped/DefinitelyTyped\n" +
                " * License: MIT\n" +
                " * Copyright:\n" +
                "   * Copyright (c) Microsoft Corporation.";
    }
    private String sampleTwoDependenciesNoticeTxt() {
        return "__@yarnpkg/lockfile 1.1.0__\n" +
                " * https://github.com/yarnpkg/yarn/blob/master/packages/lockfile\n" +
                " * License: BSD-2-Clause\n" +
                " * Copyright:\n" +
                "   * Copyright (c) 2013-present, Facebook, Inc.\n" +
                "   * copyright (c) 2018 Denis Pushkarev\n" +
                "   * Copyright Joyent, Inc. and other Node contributors\n" +
                "\n" +
                "__JSONStream 1.3.5__\n" +
                " * https://github.com/dominictarr/JSONStream\n" +
                " * Licenses:\n" +
                "   * Apache-2.0\n" +
                "   * MIT\n" +
                " * Copyright:\n" +
                "   * Copyright (c) 2011 Dominic Tarr";
    }

}