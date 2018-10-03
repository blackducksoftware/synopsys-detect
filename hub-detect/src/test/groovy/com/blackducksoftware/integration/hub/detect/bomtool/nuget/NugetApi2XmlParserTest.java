package com.blackducksoftware.integration.hub.detect.bomtool.nuget;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.blackducksoftware.integration.hub.detect.bomtool.nuget.apiversion2.NugetApi2XmlParser;
import com.blackducksoftware.integration.hub.detect.testutils.TestUtil;
import com.github.zafarkhaja.semver.Version;

public class NugetApi2XmlParserTest {
    private final String INSPECTOR_NAME = "IntegrationNugetInspector";

    private NugetApi2XmlParser nugetXmlParser;
    private Document xmlDocument;

    @Before
    public void init() throws ParserConfigurationException, IOException, SAXException {
        final TestUtil testUtil = new TestUtil();
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder xmlDocumentBuilder = factory.newDocumentBuilder();
        final InputStream inputStream = testUtil.getResourceAsInputStream("/nuget/nuget_v2_response.xml");
        nugetXmlParser = new NugetApi2XmlParser();
        xmlDocument = xmlDocumentBuilder.parse(inputStream);
    }

    @Test
    public void parseVersions() {
        final List<Version> versions = nugetXmlParser.parseVersions(xmlDocument, INSPECTOR_NAME);

        assert versions != null;
        assert versions.size() == 23;
    }
}