package com.synopsys.integration.detectable.detectables.maven.functional;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.annotations.FunctionalTest;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.maven.cli.MavenCodeLocationPackager;
import com.synopsys.integration.detectable.detectables.maven.cli.MavenParseResult;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;
import com.synopsys.integration.detectable.util.GraphCompare;

@FunctionalTest
public class MavenCodeLocationPackagerFunctionalTest {
    @Test
    public void extractCodeLocationsTestWithNumbersRemovedOutput() {
        final String mavenOutputText = FunctionalTestFiles.asString("/maven/hubTeamcityOutputWithStrangePrefixesFoundFromCustomer.txt");
        createNewCodeLocationTest(mavenOutputText, "/maven/hubTeamCityCodeLocation.json", 5, "", "");
    }

    @Test
    public void extractCodeLocationsTest() {
        final String mavenOutputText = FunctionalTestFiles.asString("/maven/sonarStashOutput.txt");
        createNewCodeLocationTest(mavenOutputText, "/maven/sonarStashCodeLocation.json");
    }

    @Test
    public void extractCodeLocationsTestTeamCity() {
        final String mavenOutputText = FunctionalTestFiles.asString("/maven/hubTeamcityOutput.txt");
        createNewCodeLocationTest(mavenOutputText, "/maven/hubTeamCityCodeLocation.json", 5, "", "");
    }

    @Test
    public void extractCodeLocationsTestTeamCityWithUnpackDependencies() {
        final String mavenOutputText = FunctionalTestFiles.asString("/maven/hubTeamcityOutputWithDependencyUnpack.txt");
        createNewCodeLocationTest(mavenOutputText, "/maven/hubTeamCityCodeLocation.json", 5, "", "");
    }

    @Test
    public void extractCodeLocationsTestTeamCityIncludedModules() {
        final String mavenOutputText = FunctionalTestFiles.asString("/maven/hubTeamcityOutput.txt");
        createNewCodeLocationTest(mavenOutputText, "/maven/hubTeamCityIncludedCodeLocation.json", 1, "", "hub-teamcity-agent");
    }

    @Test
    public void extractCodeLocationsTestTeamCityExcludedModules() {
        final String mavenOutputText = FunctionalTestFiles.asString("/maven/hubTeamcityOutput.txt");
        createNewCodeLocationTest(mavenOutputText, "/maven/hubTeamCityExcludedCodeLocation.json", 1, "hub-teamcity-common,hub-teamcity-agent,hub-teamcity-assembly,hub-teamcity", "");
    }

    @Test
    public void extractCodeLocationsCorruptTest() {
        final String mavenOutputText = FunctionalTestFiles.asString("/maven/sonarStashCorruptOutput.txt");
        createNewCodeLocationTest(mavenOutputText, "/maven/sonarStashCorruptCodeLocation.json");
    }

    @Test
    public void extractCodeLocationsTestWebgoat() {
        final String mavenOutputText = FunctionalTestFiles.asString("/maven/webgoat-container-pom-dependency-tree-output.txt");
        createNewCodeLocationTest(mavenOutputText, "/maven/webgoatCodeLocation.json", 1, "", "");
    }

    @Test
    public void extractCodeLocationsTestNoScope() {
        final String mavenOutputText = FunctionalTestFiles.asString("/maven/compileScopeUnderTestScope.txt");
        createNewCodeLocationTest(mavenOutputText, "/maven/compileScopeUnderTestScopeNoScope.json", 3, "", "", 2, null);
    }

    @Test
    public void extractCodeLocationsTestCompileScope() {
        final String mavenOutputText = FunctionalTestFiles.asString("/maven/compileScopeUnderTestScope.txt");
        createNewCodeLocationTest(mavenOutputText, "/maven/compileScopeUnderTestScopeCompileScope.json", 3, "", "", 2, "compile");
    }

    private void createNewCodeLocationTest(final String mavenOutputText, final String expectedResourcePath) {
        createNewCodeLocationTest(mavenOutputText, expectedResourcePath, 1, "", "");
    }

    private void createNewCodeLocationTest(final String mavenOutputText, final String expectedResourcePath, final int numberOfCodeLocations, final String excludedModules, final String includedModules) {
        createNewCodeLocationTest(mavenOutputText, expectedResourcePath, numberOfCodeLocations, excludedModules, includedModules, 0);
    }

    private void createNewCodeLocationTest(final String mavenOutputText, final String expectedResourcePath, final int numberOfCodeLocations, final String excludedModules, final String includedModules, final int codeLocationIndex) {
        createNewCodeLocationTest(mavenOutputText, expectedResourcePath, numberOfCodeLocations, excludedModules, includedModules, codeLocationIndex, null);
    }

    private void createNewCodeLocationTest(final String mavenOutputText, final String expectedResourcePath, final int numberOfCodeLocations, final String excludedModules, final String includedModules, final int codeLocationIndex,
        final String scope) {
        final MavenCodeLocationPackager mavenCodeLocationPackager = new MavenCodeLocationPackager(new ExternalIdFactory());
        final List<MavenParseResult> result = mavenCodeLocationPackager.extractCodeLocations("/test/path", mavenOutputText, scope, excludedModules, includedModules);
        assertEquals(numberOfCodeLocations, result.size());
        final CodeLocation codeLocation = result.get(codeLocationIndex).codeLocation;

        GraphCompare.assertEqualsResource(expectedResourcePath, codeLocation.getDependencyGraph());
    }
}
