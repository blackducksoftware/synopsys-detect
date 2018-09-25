package com.blackducksoftware.integration.hub.detect.bomtool.nuget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.blackducksoftware.integration.hub.detect.bomtool.nuget.apiversion3.NugetApi3CatalogEntry;
import com.blackducksoftware.integration.hub.detect.bomtool.nuget.apiversion3.NugetApi3CatalogPage;
import com.blackducksoftware.integration.hub.detect.bomtool.nuget.apiversion3.NugetApi3Package;
import com.blackducksoftware.integration.hub.detect.bomtool.nuget.apiversion3.NugetApi3RegistrationJsonParser;
import com.blackducksoftware.integration.hub.detect.bomtool.nuget.apiversion3.NugetApi3Response;
import com.blackducksoftware.integration.hub.detect.testutils.TestUtil;
import com.github.zafarkhaja.semver.Version;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class NugetApi3RegistrationJsonParserTest {
    private final String INSPECTOR_NAME = "IntegrationNugetInspector";

    private final TestUtil testUtil = new TestUtil();

    private NugetApi3RegistrationJsonParser nugetJsonParser;

    @Before
    public void init() {
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        nugetJsonParser = new NugetApi3RegistrationJsonParser(gson);

        final NugetApi3CatalogEntry synopsysNugetCatalogEntry = new NugetApi3CatalogEntry();
        synopsysNugetCatalogEntry.setId("https://api.nuget.org/v3/catalog0/data/2018.07.27.16.24.26/integrationnugetinspector.2.5.0.json");
        synopsysNugetCatalogEntry.setAuthors("Black Duck by Synopsys");
        synopsysNugetCatalogEntry.setPackageName("IntegrationNugetInspector");
        synopsysNugetCatalogEntry.setPackageVersion("2.5.0");
        final NugetApi3Package synopsysPackage = new NugetApi3Package();
        synopsysPackage.setCatalogEntry(synopsysNugetCatalogEntry);

        final NugetApi3CatalogEntry blackDuckNugetCatalogEntry = new NugetApi3CatalogEntry();
        blackDuckNugetCatalogEntry.setId("https://api.nuget.org/v3/catalog0/data/2018.06.04.19.39.54/integrationnugetinspector.2.4.0.json");
        blackDuckNugetCatalogEntry.setAuthors("Black Duck Software");
        blackDuckNugetCatalogEntry.setPackageName("IntegrationNugetInspector");
        blackDuckNugetCatalogEntry.setPackageVersion("2.4.0");
        final NugetApi3Package blackDuckPackage = new NugetApi3Package();
        blackDuckPackage.setCatalogEntry(blackDuckNugetCatalogEntry);

        final NugetApi3CatalogEntry invalidAuthorNugetCatalogEntry = new NugetApi3CatalogEntry();
        invalidAuthorNugetCatalogEntry.setId("something else");
        invalidAuthorNugetCatalogEntry.setAuthors("Some other company");
        invalidAuthorNugetCatalogEntry.setPackageName("IntegrationNugetInspector");
        invalidAuthorNugetCatalogEntry.setPackageVersion("2.5.0");
        final NugetApi3Package invalidAuthorPackage = new NugetApi3Package();
        invalidAuthorPackage.setCatalogEntry(invalidAuthorNugetCatalogEntry);

        final NugetApi3CatalogEntry invalidPackageNugetCatalogEntry = new NugetApi3CatalogEntry();
        invalidPackageNugetCatalogEntry.setId("something else");
        invalidPackageNugetCatalogEntry.setAuthors("Black Duck by Synopsys");
        invalidPackageNugetCatalogEntry.setPackageName("SomeOtherPackage");
        invalidPackageNugetCatalogEntry.setPackageVersion("2.5.0");
        final NugetApi3Package invalidPackage = new NugetApi3Package();
        invalidPackage.setCatalogEntry(invalidPackageNugetCatalogEntry);

        final NugetApi3CatalogPage synopsysCatalogPage = new NugetApi3CatalogPage();
        synopsysCatalogPage.setItems(new ArrayList<>(Arrays.asList(
            synopsysPackage,
            blackDuckPackage
        )));

        final NugetApi3CatalogPage otherCatalogPage = new NugetApi3CatalogPage();
        otherCatalogPage.setItems(new ArrayList<>(Arrays.asList(
            invalidAuthorPackage,
            invalidPackage
        )));

        final NugetApi3Response nugetResponse = new NugetApi3Response();
        nugetResponse.setItems(new ArrayList<>(Arrays.asList(
            synopsysCatalogPage,
            otherCatalogPage
        )));
    }

    @Test
    public void parseNugetResponseTest() {
        final String jsonResponse = testUtil.getResourceAsUTF8String("/nuget/nuget_v3_response.json");
        final List<Version> versions = nugetJsonParser.findInspectionVersions(jsonResponse, INSPECTOR_NAME);

        assert versions.size() == 23;
    }
}
