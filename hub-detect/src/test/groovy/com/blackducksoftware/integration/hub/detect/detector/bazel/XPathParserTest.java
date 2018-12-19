package com.blackducksoftware.integration.hub.detect.detector.bazel;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.xml.sax.SAXException;

public class XPathParserTest {

    @Test
    public void test1() throws Exception {
        final String xmlFilePath = "src/test/resources/bazel/sample1.xml";
        parseXmlFile(xmlFilePath);
    }

    @Test
    public void test2() throws Exception {
        final String xmlFilePath = "src/test/resources/bazel/sample2.xml";
        parseXmlFile(xmlFilePath);
    }

    private void parseXmlFile(final String xmlFilePath) throws IOException, SAXException, ParserConfigurationException, XPathExpressionException {
        final String xml = FileUtils.readFileToString(new File(xmlFilePath), StandardCharsets.UTF_8);
        XPathParser parser = new XPathParser(xml);
        parser.parse("/query/rule/string[@name='artifact']", "value");
    }

}
