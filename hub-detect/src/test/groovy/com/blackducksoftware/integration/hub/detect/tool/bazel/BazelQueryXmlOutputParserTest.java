package com.blackducksoftware.integration.hub.detect.tool.bazel;

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
    public void testFlexibleSimpleXml() throws Exception {
        final String xmlFilePath = "src/test/resources/bazel/sample1.xml";
        List<String> externalIds = getExternalIdsFromXmlFile(xmlFilePath);
        assertEquals(1, externalIds.size());
        assertTrue(externalIds.contains("com.google.guava:guava:18.0"));
    }

    @Test
    public void testFlexibleMoreComplexXml() throws Exception {
        final String xmlFilePath = "src/test/resources/bazel/sample2.xml";
        List<String> externalIds = getExternalIdsFromXmlFile(xmlFilePath);
        assertEquals(2, externalIds.size());
        assertTrue(externalIds.contains("org.apache.commons:commons-io:1.3.2"));
        assertTrue(externalIds.contains("com.google.guava:guava:18.0"));
    }

    private List<String> getExternalIdsFromXmlFile(final String xmlFilePath) throws IOException, XPathExpressionException, ParserConfigurationException, SAXException {
        final String xml = FileUtils.readFileToString(new File(xmlFilePath), StandardCharsets.UTF_8);
        final XPathParser xPathParser = new XPathParser();
        BazelQueryXmlOutputParser parser = new BazelQueryXmlOutputParser(xPathParser);
        String xPathQuery = "/query/rule[@class='maven_jar']/string[@name='artifact']";
        return parser.parseStringValuesWithXPath(xml, xPathQuery, "value");
    }
}
