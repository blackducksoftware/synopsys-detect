package com.blackducksoftware.integration.hub.detect.bomtool.nuget

import com.blackducksoftware.integration.hub.detect.bomtool.nuget.api2.NugetXmlParser
import com.blackducksoftware.integration.hub.detect.testutils.TestUtil
import com.github.zafarkhaja.semver.Version
import org.junit.Before
import org.junit.Test
import org.w3c.dom.Document

import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory

class NugetXmlParserTest {
    private final static INSPECTOR_NAME = "IntegrationNugetInspector"

    NugetXmlParser nugetXmlParser
    Document xmlDocument

    @Before
    void init() {
        final TestUtil testUtil = new TestUtil()
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance()
        final DocumentBuilder xmlDocumentBuilder = factory.newDocumentBuilder()
        final InputStream inputStream = testUtil.getResourceAsInputStream("/nuget/nuget_v2_response.xml")
        nugetXmlParser = new NugetXmlParser()
        xmlDocument = xmlDocumentBuilder.parse(inputStream)
    }

    @Test
    void parseVersions() {
        final List<Version> versions = nugetXmlParser.parseVersions(xmlDocument, INSPECTOR_NAME)

        assert versions != null
        assert versions.size() == 23
    }
}