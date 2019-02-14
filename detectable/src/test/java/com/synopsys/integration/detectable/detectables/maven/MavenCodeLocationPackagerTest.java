package com.synopsys.integration.detectable.detectables.maven;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;
import com.synopsys.integration.detectable.util.GraphCompare;

public class MavenCodeLocationPackagerTest {
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

    @Test
    public void testParseProject() {
        final MavenCodeLocationPackager mavenCodeLocationPackager = new MavenCodeLocationPackager(new ExternalIdFactory());

        Dependency dependency = mavenCodeLocationPackager.textToProject("stuff:things:jar:0.0.1");
        assertNotNull(dependency);

        dependency = mavenCodeLocationPackager.textToProject("stuff:things:jar:classifier:0.0.1");
        assertNotNull(dependency);

        dependency = mavenCodeLocationPackager.textToProject("stuff:things:jar");
        assertNull(dependency);

        dependency = mavenCodeLocationPackager.textToProject("stuff:things:jar:classifier:0.0.1:monkey");
        assertNull(dependency);
    }

    @Test
    public void testParseDependency() {
        final MavenCodeLocationPackager mavenCodeLocationPackager = new MavenCodeLocationPackager(new ExternalIdFactory());

        ScopedDependency dependency = mavenCodeLocationPackager.textToDependency("stuff:things:jar:0.0.1:compile");
        assertNotNull(dependency);

        dependency = mavenCodeLocationPackager.textToDependency("stuff:things:jar:classifier:0.0.1:test");
        assertNotNull(dependency);

        dependency = mavenCodeLocationPackager.textToDependency("stuff:things:jar");
        assertNull(dependency);

        dependency = mavenCodeLocationPackager.textToDependency("stuff:things:jar:classifier:0.0.1");
        assertNotNull(dependency);
    }

    @Test
    public void testIsLineRelevant() {
        final MavenCodeLocationPackager mavenCodeLocationPackager = new MavenCodeLocationPackager(null);

        assertTrue(mavenCodeLocationPackager.isLineRelevant("weird garbage 3525356 [thingsINFO 346534623465] stuff"));

        assertTrue(mavenCodeLocationPackager.isLineRelevant("[thingsINFO 346534623465]stuff"));

        assertTrue(mavenCodeLocationPackager.isLineRelevant("[thingsINFO]  stuff"));

        assertFalse(mavenCodeLocationPackager.isLineRelevant(" [INFO]     "));

        assertFalse(mavenCodeLocationPackager.isLineRelevant("weird garbage 3525356 [thingsINFO 346534623465]"));

        assertFalse(mavenCodeLocationPackager.isLineRelevant("[thingsINFO 346534623465]"));

        assertFalse(mavenCodeLocationPackager.isLineRelevant("[thingsINFO]"));

        assertFalse(mavenCodeLocationPackager.isLineRelevant(" [INFO]"));

        assertFalse(mavenCodeLocationPackager.isLineRelevant(" "));

        assertFalse(mavenCodeLocationPackager.isLineRelevant("[INFO] Downloaded"));

        assertFalse(mavenCodeLocationPackager.isLineRelevant("[INFO] stuff and thingsDownloaded stuff and things"));

        assertFalse(mavenCodeLocationPackager.isLineRelevant("[INFO] Downloading"));

        assertFalse(mavenCodeLocationPackager.isLineRelevant("[INFO] stuff and things Downloadingstuff and things"));
    }

    @Test
    public void testTrimLogLevel() {
        final MavenCodeLocationPackager mavenCodeLocationPackager = new MavenCodeLocationPackager(null);

        String actualLine = "";
        final String expectedValue = "thing";

        actualLine = mavenCodeLocationPackager.trimLogLevel("weird garbage 3525356 [thingsINFO 346534623465]" + expectedValue);
        assertEquals(expectedValue, actualLine);

        actualLine = mavenCodeLocationPackager.trimLogLevel("[thingsINFO 346534623465]" + expectedValue);
        assertEquals(expectedValue, actualLine);

        actualLine = mavenCodeLocationPackager.trimLogLevel("[thingsINFO]" + expectedValue);
        assertEquals(expectedValue, actualLine);

        actualLine = mavenCodeLocationPackager.trimLogLevel(" [INFO] " + expectedValue);
        assertEquals(expectedValue, actualLine);
    }

    @Test
    public void testIsProjectSection() {
        final MavenCodeLocationPackager mavenCodeLocationPackager = new MavenCodeLocationPackager(null);

        assertFalse(mavenCodeLocationPackager.isProjectSection(" "));

        assertFalse(mavenCodeLocationPackager.isProjectSection("       "));

        assertFalse(mavenCodeLocationPackager.isProjectSection("---maven-dependency-plugin:"));

        assertFalse(mavenCodeLocationPackager.isProjectSection("---maven-dependency-plugin:other      stuff"));

        assertFalse(mavenCodeLocationPackager.isProjectSection("maven-dependency-plugin:tree      stuff"));

        assertTrue(mavenCodeLocationPackager.isProjectSection("---maven-dependency-plugin:tree      stuff"));

        assertTrue(mavenCodeLocationPackager.isProjectSection("things --- stuff maven-dependency-plugin garbage:tree      stuff"));

        assertTrue(mavenCodeLocationPackager.isProjectSection("things --- stuff maven-dependency-plugin:tree      stuff"));

        assertTrue(mavenCodeLocationPackager.isProjectSection("---maven-dependency-plugin:tree"));

        assertTrue(mavenCodeLocationPackager.isProjectSection("      ---       maven-dependency-plugin      :       tree"));
    }

    @Test
    public void testIsDependencyTreeUpdates() {
        final MavenCodeLocationPackager mavenCodeLocationPackager = new MavenCodeLocationPackager(null);

        assertTrue(mavenCodeLocationPackager.isDependencyTreeUpdates("artifact com.google.guava:guava:jar:15.0:compile checking for updates from"));

        assertTrue(mavenCodeLocationPackager.isDependencyTreeUpdates("         artifact       com.google.guava:guava:         checking for updates"));

        assertTrue(mavenCodeLocationPackager.isDependencyTreeUpdates("      checking for updates   artifact       com.google.guava:guava:      "));

        assertTrue(mavenCodeLocationPackager.isDependencyTreeUpdates("checking for updates"));

        assertFalse(mavenCodeLocationPackager.isDependencyTreeUpdates("com.google.guava:guava:jar:15.0:compile"));

        assertFalse(mavenCodeLocationPackager.isDependencyTreeUpdates("+- com.google.guava:guava:jar:15.0:compile"));

        assertFalse(mavenCodeLocationPackager.isDependencyTreeUpdates("|  \\- com.google.guava:guava:jar:15.0:compile"));
    }

    @Test
    public void testIsGav() {
        final MavenCodeLocationPackager mavenCodeLocationPackager = new MavenCodeLocationPackager(null);

        assertFalse(mavenCodeLocationPackager.isGav(" "));

        assertFalse(mavenCodeLocationPackager.isGav("       "));

        assertFalse(mavenCodeLocationPackager.isGav("::::"));

        assertFalse(mavenCodeLocationPackager.isGav(" : : : : "));

        assertFalse(mavenCodeLocationPackager.isGav("group"));

        assertFalse(mavenCodeLocationPackager.isGav("group:artifact"));

        assertFalse(mavenCodeLocationPackager.isGav("group:artifact:version"));

        assertFalse(mavenCodeLocationPackager.isGav("group-artifact:type-classifier-version:scope-garbage"));

        assertFalse(mavenCodeLocationPackager.isGav("group:artifact::classifier:version: :garbage"));

        assertTrue(mavenCodeLocationPackager.isGav("group:artifact:type:version"));

        assertTrue(mavenCodeLocationPackager.isGav("group:artifact:type:classifier:version"));

        assertTrue(mavenCodeLocationPackager.isGav("group:artifact:type:classifier:version:scope"));

        assertTrue(mavenCodeLocationPackager.isGav("group:artifact:type:classifier:version:scope:garbage"));
    }

    @Test
    public void testIndexOfEndOfSegments() {
        final MavenCodeLocationPackager mavenCodeLocationPackager = new MavenCodeLocationPackager(null);

        assertEquals(-1, mavenCodeLocationPackager.indexOfEndOfSegments(""));

        assertEquals(-1, mavenCodeLocationPackager.indexOfEndOfSegments("stuff and things"));

        assertEquals(-1, mavenCodeLocationPackager.indexOfEndOfSegments("stuff and things", "things", "and"));

        assertEquals(-1, mavenCodeLocationPackager.indexOfEndOfSegments("stuff and things", "things", "and", "stuff"));

        assertEquals(5, mavenCodeLocationPackager.indexOfEndOfSegments("stuff and things", "stuff"));

        assertEquals(9, mavenCodeLocationPackager.indexOfEndOfSegments("stuff and things", "stuff", "and"));

        assertEquals(16, mavenCodeLocationPackager.indexOfEndOfSegments("stuff and things", "stuff", "and", "things"));

        assertEquals(9, mavenCodeLocationPackager.indexOfEndOfSegments("stuff and things", "and"));

        assertEquals(16, mavenCodeLocationPackager.indexOfEndOfSegments("stuff and things", "things"));
    }

    @Test
    public void testDoesLineContainSegmentsInOrder() {
        final MavenCodeLocationPackager mavenCodeLocationPackager = new MavenCodeLocationPackager(null);

        assertFalse(mavenCodeLocationPackager.doesLineContainSegmentsInOrder(""));

        assertFalse(mavenCodeLocationPackager.doesLineContainSegmentsInOrder("stuff and things"));

        assertFalse(mavenCodeLocationPackager.doesLineContainSegmentsInOrder("stuff and things", "things", "and"));

        assertFalse(mavenCodeLocationPackager.doesLineContainSegmentsInOrder("stuff and things", "things", "and", "stuff"));

        assertTrue(mavenCodeLocationPackager.doesLineContainSegmentsInOrder("stuff and things", "stuff"));

        assertTrue(mavenCodeLocationPackager.doesLineContainSegmentsInOrder("stuff and things", "stuff", "and"));

        assertTrue(mavenCodeLocationPackager.doesLineContainSegmentsInOrder("stuff and things", "stuff", "and", "things"));

        assertTrue(mavenCodeLocationPackager.doesLineContainSegmentsInOrder("stuff and things", "and"));

        assertTrue(mavenCodeLocationPackager.doesLineContainSegmentsInOrder("stuff and things", "things"));
    }

    @Test
    public void testLineWithExtraTextAfterScope() {
        final MavenCodeLocationPackager mavenCodeLocationPackager = new MavenCodeLocationPackager(new ExternalIdFactory());

        String line = "[INFO] |  |  |  \\- org.eclipse.scout.sdk.deps:org.eclipse.core.jobs:jar:3.8.0.v20160509-0411:compile (version selected from constraint [3.8.0,3.8.1))";
        line = mavenCodeLocationPackager.trimLogLevel(line);
        final String cleanedLine = mavenCodeLocationPackager.calculateCurrentLevelAndCleanLine(line);
        final Dependency dependency = mavenCodeLocationPackager.textToDependency(cleanedLine);
        assertEquals("org.eclipse.scout.sdk.deps:org.eclipse.core.jobs:3.8.0.v20160509-0411", dependency.externalId.createExternalId());
    }

    @Test
    public void testLineWithUnknownScope() {
        final MavenCodeLocationPackager mavenCodeLocationPackager = new MavenCodeLocationPackager(new ExternalIdFactory());

        String line = "[INFO] |  |  |  \\- org.eclipse.scout.sdk.deps:org.eclipse.core.jobs:jar:3.8.0.v20160509-0411:pants (version selected from constraint [3.8.0,3.8.1))";
        line = mavenCodeLocationPackager.trimLogLevel(line);
        final String cleanedLine = mavenCodeLocationPackager.calculateCurrentLevelAndCleanLine(line);
        final ScopedDependency scopedDependency = mavenCodeLocationPackager.textToDependency(cleanedLine);
        assertEquals("org.eclipse.scout.sdk.deps:org.eclipse.core.jobs:3.8.0.v20160509-0411", scopedDependency.externalId.createExternalId());
    }

    @Test
    public void testLineWithBadColonPlacement() {
        final MavenCodeLocationPackager mavenCodeLocationPackager = new MavenCodeLocationPackager(new ExternalIdFactory());

        String line = "[INFO] |  |  |  \\- org.eclipse.scout.sdk.deps:org.eclipse.core.jobs:jar:3.8.0.v20160509-0411:pants (version selected from: [3.8.0,3.8.1))";
        line = mavenCodeLocationPackager.trimLogLevel(line);
        final String cleanedLine = mavenCodeLocationPackager.calculateCurrentLevelAndCleanLine(line);
        final Dependency dependency = mavenCodeLocationPackager.textToDependency(cleanedLine);
        assertEquals("org.eclipse.scout.sdk.deps:org.eclipse.core.jobs:pants (version selected from", dependency.externalId.createExternalId());
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
