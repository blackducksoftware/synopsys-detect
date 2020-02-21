package com.synopsys.integration.detectable.detectables.bazel.functional.bazel;

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

import com.synopsys.integration.detectable.detectables.bazel.parse.BazelQueryXmlOutputParser;
import com.synopsys.integration.detectable.detectables.bazel.parse.XPathParser;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;

public class BazelQueryXmlOutputParserTest {

    @Test
    public void testFlexibleSimpleXml() throws Exception {
        final String xmlFilePath = FunctionalTestFiles.asString("/bazel/sample1.xml");
        List<String> externalIds = getExternalIdsFromXmlFile(xmlFilePath);
        assertEquals(1, externalIds.size());
        assertTrue(externalIds.contains("maven_coordinates=com.google.guava:guava:24.0-jre"));
    }

    @Test
    public void testFlexibleMoreComplexXml() throws Exception {
        final String xmlFilePath = FunctionalTestFiles.asString("/bazel/sample2.xml");
        List<String> externalIds = getExternalIdsFromXmlFile(xmlFilePath);
        assertEquals(1, externalIds.size());
        assertTrue(externalIds.contains("maven_coordinates=com.google.guava:guava:24.0-jre"));
    }

    private List<String> getExternalIdsFromXmlFile(final String xml) throws IOException, XPathExpressionException, ParserConfigurationException, SAXException {
        final XPathParser xPathParser = new XPathParser();
        BazelQueryXmlOutputParser parser = new BazelQueryXmlOutputParser(xPathParser);
        String xPathQuery = "/query/rule[@class='jvm_import']/list[@name='tags']/string";
        return parser.parseStringValuesWithXPath(xml, xPathQuery, "value");
    }
}
