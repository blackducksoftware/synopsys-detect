package com.blackducksoftware.integration.hub.detect.bomtool.nuget

import com.blackducksoftware.integration.hub.detect.BeanConfiguration
import com.blackducksoftware.integration.hub.detect.bomtool.nuget.api2.NugetEntry
import com.blackducksoftware.integration.hub.detect.bomtool.nuget.api2.NugetXmlParser
import com.blackducksoftware.integration.hub.detect.testutils.TestUtil
import com.github.zafarkhaja.semver.Version
import org.junit.Before
import org.junit.Test
import org.w3c.dom.Document

import javax.xml.parsers.DocumentBuilder

class NugetXmlParserTest {
    private final static INSPECTOR_NAME = "IntegrationNugetInspector"

    NugetXmlParser nugetXmlParser
    Document xmlDocument;

    @Before
    void init() {
        final TestUtil testUtil = new TestUtil()
        final BeanConfiguration beanConfiguration = new BeanConfiguration(null)
        final DocumentBuilder xmlDocumentBuilder = beanConfiguration.xmlDocumentBuilder()
        final InputStream inputStream = testUtil.getResourceAsInputStream("/nuget/nuget_v2_response.xml")
        xmlDocument = xmlDocumentBuilder.parse(inputStream)
    }

    @Test
    void parseVersions() {
        final List<Version> versions = nugetXmlParser.parseVersions(xmlDocument, INSPECTOR_NAME)

        assert versions != null
        assert versions.size() == 23
    }

    @Test
    void transformNugetEntries() {
        final NugetEntry nugetEntry1 = new NugetEntry("stuff", Arrays.asList("Black Duck by Synopsys"), INSPECTOR_NAME, "1.2.3")
        final NugetEntry nugetEntry2 = new NugetEntry("other stuff", Arrays.asList("Black Duck by Synopsys"), INSPECTOR_NAME, "4.5.6")
        final List<NugetEntry> nugetEntries = new ArrayList<>(Arrays.asList(nugetEntry1, nugetEntry2))
        final List<Version> versions = nugetXmlParser.transformNugetEntries(nugetEntries, INSPECTOR_NAME)

        assert versions != null
        assert versions.size() == 2
        assert versions.get(0) == Version.forIntegers(1, 2, 3)
        assert versions.get(1) == Version.forIntegers(4, 5, 6)
    }

    @Test
    void isBlackDuckPackage() {
        // All good
        final NugetEntry nugetEntry1 = new NugetEntry("stuff", Arrays.asList("Black Duck by Synopsys"), INSPECTOR_NAME, "1.2.3")
        final NugetEntry nugetEntry2 = new NugetEntry("other stuff", Arrays.asList("Black Duck Software"), INSPECTOR_NAME, "4.5.6")
        assert nugetXmlParser.isBlackDuckPackage(nugetEntry1, INSPECTOR_NAME)
        assert nugetXmlParser.isBlackDuckPackage(nugetEntry2, INSPECTOR_NAME)

        // Incorrect package name
        final NugetEntry nugetEntry3 = new NugetEntry("stuff", Arrays.asList("Black Duck by Synopsys"), "Bad Package Name", "1.2.3")
        final NugetEntry nugetEntry4 = new NugetEntry("other stuff", Arrays.asList("Black Duck Software"), "Bad Package Name", "4.5.6")
        assert !nugetXmlParser.isBlackDuckPackage(nugetEntry3, INSPECTOR_NAME)
        assert !nugetXmlParser.isBlackDuckPackage(nugetEntry4, INSPECTOR_NAME)

        // Incorrect Author
        final NugetEntry nugetEntry5 = new NugetEntry("more stuff", Arrays.asList("Wrong Author"), "Bad Package Name", "7.8.9")
        final NugetEntry nugetEntry6 = new NugetEntry("other stuff", Arrays.asList("Wrong Author"), INSPECTOR_NAME, "4.5.6")
        assert !nugetXmlParser.isBlackDuckPackage(nugetEntry5, INSPECTOR_NAME)
        assert !nugetXmlParser.isBlackDuckPackage(nugetEntry6, INSPECTOR_NAME)

        // Incorrect everything
        final NugetEntry nugetEntry7 = new NugetEntry("other stuff", Arrays.asList("Wrong Author"), "Bad Package Name", "4.5.6")
        final NugetEntry nugetEntry8 = new NugetEntry("other stuff", Arrays.asList("Black Duck by Synopsys", "Wrong Author"), "Bad Package Name", "4.5.6")
        assert !nugetXmlParser.isBlackDuckPackage(nugetEntry7, INSPECTOR_NAME)
        assert !nugetXmlParser.isBlackDuckPackage(nugetEntry8, INSPECTOR_NAME)
    }

    @Test
    void transformNugetEntry() {
        final NugetEntry nugetEntry = new NugetEntry("stuff", Arrays.asList("Black Duck by Synopsys"), INSPECTOR_NAME, "1.2.3")
        final Version version = nugetXmlParser.transformNugetEntry(nugetEntry)

        assert version == Version.forIntegers(1, 2, 3)
    }

    @Test
    void parseEntries() {
        final List<NugetEntry> nugetEntries = nugetXmlParser.parseEntries(xmlDocument)

        assert nugetEntries != null
        assert nugetEntries.size() == 23
    }
}