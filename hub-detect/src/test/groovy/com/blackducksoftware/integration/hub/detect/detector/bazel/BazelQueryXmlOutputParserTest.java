package com.blackducksoftware.integration.hub.detect.detector.bazel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.xml.sax.SAXException;

public class BazelQueryXmlOutputParserTest {

    @Test
    public void testConstrainedSimpleXml() throws Exception {
        final String xmlFilePath = "src/test/resources/bazel/sample1.xml";
        List<String> externalIds = getExternalIdsFromXmlFileConstrained(xmlFilePath);
        assertEquals(1, externalIds.size());
        assertTrue(externalIds.contains("com.google.guava:guava:18.0"));
    }

    @Test
    public void testConstrainedMoreComplexXml() throws Exception {
        final String xmlFilePath = "src/test/resources/bazel/sample2.xml";
        List<String> externalIds = getExternalIdsFromXmlFileConstrained(xmlFilePath);
        assertEquals(2, externalIds.size());
        assertTrue(externalIds.contains("org.apache.commons:commons-io:1.3.2"));
        assertTrue(externalIds.contains("com.google.guava:guava:18.0"));
    }

    @Test
    public void testFlexibleSimpleXml() throws Exception {
        final String xmlFilePath = "src/test/resources/bazel/sample1.xml";
        List<String> externalIds = getExternalIdsFromXmlFileFlexible(xmlFilePath);
        assertEquals(1, externalIds.size());
        assertTrue(externalIds.contains("com.google.guava:guava:18.0"));
    }

    @Test
    public void testFlexibleMoreComplexXml() throws Exception {
        final String xmlFilePath = "src/test/resources/bazel/sample2.xml";
        List<String> externalIds = getExternalIdsFromXmlFileFlexible(xmlFilePath);
        assertEquals(2, externalIds.size());
        assertTrue(externalIds.contains("org.apache.commons:commons-io:1.3.2"));
        assertTrue(externalIds.contains("com.google.guava:guava:18.0"));
    }

    private List<String> getExternalIdsFromXmlFileConstrained(final String xmlFilePath) throws IOException, XPathExpressionException, ParserConfigurationException, SAXException {
        final String xml = FileUtils.readFileToString(new File(xmlFilePath), StandardCharsets.UTF_8);
        final XPathParser xPathParser = new XPathParser();
        BazelQueryXmlOutputParser parser = new BazelQueryXmlOutputParser(xPathParser);
        return parser.parseStringValuesFromRulesConstrained(xml,"maven_jar", "artifact");
    }

    private List<String> getExternalIdsFromXmlFileFlexible(final String xmlFilePath) throws IOException, XPathExpressionException, ParserConfigurationException, SAXException {
        final String xml = FileUtils.readFileToString(new File(xmlFilePath), StandardCharsets.UTF_8);
        final XPathParser xPathParser = new XPathParser();
        BazelQueryXmlOutputParser parser = new BazelQueryXmlOutputParser(xPathParser);
        return parser.parseStringValuesFromRules(xml,"maven_jar", "string", "name", "artifact", "value");
    }
}
