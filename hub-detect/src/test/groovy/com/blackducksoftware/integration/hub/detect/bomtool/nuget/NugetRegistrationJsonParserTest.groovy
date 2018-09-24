package com.blackducksoftware.integration.hub.detect.bomtool.nuget

import com.blackducksoftware.integration.hub.detect.bomtool.nuget.api3.*
import com.blackducksoftware.integration.hub.detect.testutils.TestUtil
import com.github.zafarkhaja.semver.Version
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.junit.Before
import org.junit.Test

class NugetRegistrationJsonParserTest {
    private final static INSPECTOR_NAME = "IntegrationNugetInspector"

    private final TestUtil testUtil = new TestUtil()

    private NugetRegistrationJsonParser nugetJsonParser
    private NugetResponse nugetResponse

    NugetCatalogEntry synopsysNugetCatalogEntry
    NugetCatalogEntry blackDuckNugetCatalogEntry
    NugetCatalogEntry invalidAuthorNugetCatalogEntry
    NugetCatalogEntry invalidPackageNugetCatalogEntry

    @Before
    void init() {
        final Gson gson = new GsonBuilder().setPrettyPrinting().create()
        nugetJsonParser = new NugetRegistrationJsonParser(gson)

        synopsysNugetCatalogEntry = new NugetCatalogEntry()
        synopsysNugetCatalogEntry.setId("https://api.nuget.org/v3/catalog0/data/2018.07.27.16.24.26/integrationnugetinspector.2.5.0.json")
        synopsysNugetCatalogEntry.setAuthors("Black Duck by Synopsys")
        synopsysNugetCatalogEntry.setPackageName("IntegrationNugetInspector")
        synopsysNugetCatalogEntry.setPackageVersion("2.5.0")
        final NugetPackage synopsysPackage = new NugetPackage()
        synopsysPackage.setCatalogEntry(synopsysNugetCatalogEntry)

        blackDuckNugetCatalogEntry = new NugetCatalogEntry()
        blackDuckNugetCatalogEntry.setId("https://api.nuget.org/v3/catalog0/data/2018.06.04.19.39.54/integrationnugetinspector.2.4.0.json")
        blackDuckNugetCatalogEntry.setAuthors("Black Duck Software")
        blackDuckNugetCatalogEntry.setPackageName("IntegrationNugetInspector")
        blackDuckNugetCatalogEntry.setPackageVersion("2.4.0")
        final NugetPackage blackDuckPackage = new NugetPackage()
        blackDuckPackage.setCatalogEntry(blackDuckNugetCatalogEntry)

        invalidAuthorNugetCatalogEntry = new NugetCatalogEntry()
        invalidAuthorNugetCatalogEntry.setId("something else")
        invalidAuthorNugetCatalogEntry.setAuthors("Some other company")
        invalidAuthorNugetCatalogEntry.setPackageName("IntegrationNugetInspector")
        invalidAuthorNugetCatalogEntry.setPackageVersion("2.5.0")
        final NugetPackage invalidAuthorPackage = new NugetPackage()
        invalidAuthorPackage.setCatalogEntry(invalidAuthorNugetCatalogEntry)

        invalidPackageNugetCatalogEntry = new NugetCatalogEntry()
        invalidPackageNugetCatalogEntry.setId("something else")
        invalidPackageNugetCatalogEntry.setAuthors("Black Duck by Synopsys")
        invalidPackageNugetCatalogEntry.setPackageName("SomeOtherPackage")
        invalidPackageNugetCatalogEntry.setPackageVersion("2.5.0")
        final NugetPackage invalidPackage = new NugetPackage()
        invalidPackage.setCatalogEntry(invalidPackageNugetCatalogEntry)

        final NugetCatalogPage synopsysCatalogPage = new NugetCatalogPage()
        synopsysCatalogPage.setItems(new ArrayList<NugetPackage>(Arrays.asList(
            synopsysPackage,
            blackDuckPackage,
        )))

        final NugetCatalogPage otherCatalogPage = new NugetCatalogPage()
        otherCatalogPage.setItems(new ArrayList<NugetPackage>(Arrays.asList(
            invalidAuthorPackage,
            invalidPackage
        )))

        nugetResponse = new NugetResponse()
        nugetResponse.setItems(new ArrayList<NugetCatalogPage>(Arrays.asList(
            synopsysCatalogPage,
            otherCatalogPage
        )))
    }

    @Test
    void parseNugetResponseTest() {
        final String jsonResponse = testUtil.getResourceAsUTF8String("/nuget/nuget_v3_response.json")
        final List<Version> versions = nugetJsonParser.parseNugetResponse(jsonResponse, INSPECTOR_NAME)

        assert versions.size() == 23
    }

    @Test
    void getVersionsFromNugetResponseTest() {
        final List<Version> allVersions = nugetJsonParser.getVersionsFromNugetResponse(nugetResponse, INSPECTOR_NAME)

        assert allVersions.size() == 2
    }

    @Test
    void getVersionFromCatalogEntryTest() {
        final Optional<Version> version = nugetJsonParser.getVersionFromCatalogEntry(synopsysNugetCatalogEntry, INSPECTOR_NAME)
        assert version.isPresent()
        assert version.get().toString() == "2.5.0"

        final Optional<Version> noVersion = nugetJsonParser.getVersionFromCatalogEntry(invalidPackageNugetCatalogEntry, INSPECTOR_NAME)
        assert !noVersion.isPresent()
    }

    @Test
    void isBlackDuckCatalogEntryTest() {
        assert nugetJsonParser.isBlackDuckCatalogEntry(synopsysNugetCatalogEntry, INSPECTOR_NAME)
        assert nugetJsonParser.isBlackDuckCatalogEntry(synopsysNugetCatalogEntry, INSPECTOR_NAME)

        assert !nugetJsonParser.isBlackDuckCatalogEntry(invalidAuthorNugetCatalogEntry, INSPECTOR_NAME)
        assert !nugetJsonParser.isBlackDuckCatalogEntry(invalidPackageNugetCatalogEntry, INSPECTOR_NAME)
    }
}
