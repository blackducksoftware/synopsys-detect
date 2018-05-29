package com.blackducksoftware.integration.hub.detect.bomtool.maven

import com.blackducksoftware.integration.hub.bdio.model.dependency.Dependency
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.maven.parse.MavenCodeLocationPackager
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.maven.parse.MavenParseResult
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation
import com.blackducksoftware.integration.hub.detect.testutils.TestUtil
import org.junit.Test

import static org.junit.Assert.*

class MavenCodeLocationPackagerTest {
    private TestUtil testUtil = new TestUtil()

    @Test
    public void extractCodeLocationsTestWithNumbersRemovedOutput() {
        final String mavenOutputText = testUtil.getResourceAsUTF8String('/maven/hubTeamcityOutputWithStrangePrefixesFoundFromCustomer.txt')
        createNewCodeLocationTest(mavenOutputText, '/maven/hubTeamCityCodeLocation.json', 5, "", "")
    }

    @Test
    public void extractCodeLocationsTest() {
        final String mavenOutputText = testUtil.getResourceAsUTF8String('/maven/sonarStashOutput.txt')
        createNewCodeLocationTest(mavenOutputText, '/maven/sonarStashCodeLocation.json')
    }

    @Test
    public void extractCodeLocationsTestTeamCity() {
        final String mavenOutputText = testUtil.getResourceAsUTF8String('/maven/hubTeamcityOutput.txt')
        createNewCodeLocationTest(mavenOutputText, '/maven/hubTeamCityCodeLocation.json', 5, "", "")
    }

    @Test
    public void extractCodeLocationsTestTeamCityWithUnpackDependencies() {
        final String mavenOutputText = testUtil.getResourceAsUTF8String('/maven/hubTeamcityOutputWithDependencyUnpack.txt')
        createNewCodeLocationTest(mavenOutputText, '/maven/hubTeamCityCodeLocation.json', 5, "", "")
    }

    @Test
    public void extractCodeLocationsTestTeamCityIncludedModules() {
        final String mavenOutputText = testUtil.getResourceAsUTF8String('/maven/hubTeamcityOutput.txt')
        createNewCodeLocationTest(mavenOutputText, '/maven/hubTeamCityIncludedCodeLocation.json', 1, "", "hub-teamcity-agent")
    }

    @Test
    public void extractCodeLocationsTestTeamCityExcludedModules() {
        final String mavenOutputText = testUtil.getResourceAsUTF8String('/maven/hubTeamcityOutput.txt')
        createNewCodeLocationTest(mavenOutputText, '/maven/hubTeamCityExcludedCodeLocation.json', 1, "hub-teamcity-common,hub-teamcity-agent,hub-teamcity-assembly,hub-teamcity", "")
    }

    @Test
    public void extractCodeLocationsCorruptTest() {
        final String mavenOutputText = testUtil.getResourceAsUTF8String('/maven/sonarStashCorruptOutput.txt')
        createNewCodeLocationTest(mavenOutputText, '/maven/sonarStashCorruptCodeLocation.json')
    }

    @Test
    public void extractCodeLocationsTestWebgoat() {
        final String mavenOutputText = testUtil.getResourceAsUTF8String('/maven/webgoat-container-pom-dependency-tree-output.txt')
        createNewCodeLocationTest(mavenOutputText, '/maven/webgoatCodeLocation.json', 1, "", "")
    }

    @Test
    public void testParseProject() {
        MavenCodeLocationPackager mavenCodeLocationPackager = new MavenCodeLocationPackager(new ExternalIdFactory())

        Dependency dependency = mavenCodeLocationPackager.textToProject("stuff:things:jar:0.0.1")
        assertNotNull(dependency)

        dependency = mavenCodeLocationPackager.textToProject("stuff:things:jar:classifier:0.0.1")
        assertNotNull(dependency)

        dependency = mavenCodeLocationPackager.textToProject("stuff:things:jar")
        assertNull(dependency)

        dependency = mavenCodeLocationPackager.textToProject("stuff:things:jar:classifier:0.0.1:monkey")
        assertNull(dependency)
    }

    @Test
    public void testParseDependency() {
        MavenCodeLocationPackager mavenCodeLocationPackager = new MavenCodeLocationPackager(new ExternalIdFactory())

        Dependency dependency = mavenCodeLocationPackager.textToDependency("stuff:things:jar:0.0.1:compile")
        assertNotNull(dependency)

        dependency = mavenCodeLocationPackager.textToDependency("stuff:things:jar:classifier:0.0.1:test")
        assertNotNull(dependency)

        dependency = mavenCodeLocationPackager.textToDependency("stuff:things:jar")
        assertNull(dependency)

        dependency = mavenCodeLocationPackager.textToDependency("stuff:things:jar:classifier:0.0.1")
        assertNotNull(dependency)
    }

    @Test
    public void testIsLineRelevant() {
        MavenCodeLocationPackager mavenCodeLocationPackager = new MavenCodeLocationPackager(null)

        assertTrue(mavenCodeLocationPackager.isLineRelevant("weird garbage 3525356 [thingsINFO 346534623465] stuff"))

        assertTrue(mavenCodeLocationPackager.isLineRelevant("[thingsINFO 346534623465]stuff"))

        assertTrue(mavenCodeLocationPackager.isLineRelevant("[thingsINFO]  stuff"))

        assertFalse(mavenCodeLocationPackager.isLineRelevant(" [INFO]     "))

        assertFalse(mavenCodeLocationPackager.isLineRelevant("weird garbage 3525356 [thingsINFO 346534623465]"))

        assertFalse(mavenCodeLocationPackager.isLineRelevant("[thingsINFO 346534623465]"))

        assertFalse(mavenCodeLocationPackager.isLineRelevant("[thingsINFO]"))

        assertFalse(mavenCodeLocationPackager.isLineRelevant(" [INFO]"))

        assertFalse(mavenCodeLocationPackager.isLineRelevant(" "))

        assertFalse(mavenCodeLocationPackager.isLineRelevant("[INFO] Downloaded"))

        assertFalse(mavenCodeLocationPackager.isLineRelevant("[INFO] stuff and thingsDownloaded stuff and things"))

        assertFalse(mavenCodeLocationPackager.isLineRelevant("[INFO] Downloading"))

        assertFalse(mavenCodeLocationPackager.isLineRelevant("[INFO] stuff and things Downloadingstuff and things"))
    }

    @Test
    public void testTrimLogLevel() {
        MavenCodeLocationPackager mavenCodeLocationPackager = new MavenCodeLocationPackager(null)

        String actualLine = "";
        String expectedValue = "thing"

        actualLine = mavenCodeLocationPackager.trimLogLevel("weird garbage 3525356 [thingsINFO 346534623465]" + expectedValue)
        assertEquals(expectedValue, actualLine);

        actualLine = mavenCodeLocationPackager.trimLogLevel("[thingsINFO 346534623465]" + expectedValue)
        assertEquals(expectedValue, actualLine);

        actualLine = mavenCodeLocationPackager.trimLogLevel("[thingsINFO]" + expectedValue)
        assertEquals(expectedValue, actualLine);

        actualLine = mavenCodeLocationPackager.trimLogLevel(" [INFO] " + expectedValue)
        assertEquals(expectedValue, actualLine);
    }

    @Test
    public void testIsProjectSection() {
        MavenCodeLocationPackager mavenCodeLocationPackager = new MavenCodeLocationPackager(null)

        assertFalse(mavenCodeLocationPackager.isProjectSection(" "))

        assertFalse(mavenCodeLocationPackager.isProjectSection("       "))

        assertFalse(mavenCodeLocationPackager.isProjectSection("---maven-dependency-plugin:"))

        assertFalse(mavenCodeLocationPackager.isProjectSection("---maven-dependency-plugin:other      stuff"))

        assertFalse(mavenCodeLocationPackager.isProjectSection("maven-dependency-plugin:tree      stuff"))

        assertTrue(mavenCodeLocationPackager.isProjectSection("---maven-dependency-plugin:tree      stuff"))

        assertTrue(mavenCodeLocationPackager.isProjectSection("things --- stuff maven-dependency-plugin garbage:tree      stuff"))

        assertTrue(mavenCodeLocationPackager.isProjectSection("things --- stuff maven-dependency-plugin:tree      stuff"))

        assertTrue(mavenCodeLocationPackager.isProjectSection("---maven-dependency-plugin:tree"))

        assertTrue(mavenCodeLocationPackager.isProjectSection("      ---       maven-dependency-plugin      :       tree"))
    }

    @Test
    public void testIsDependencyTreeUpdates() {
        MavenCodeLocationPackager mavenCodeLocationPackager = new MavenCodeLocationPackager(null)

        assertTrue(mavenCodeLocationPackager.isDependencyTreeUpdates("artifact com.google.guava:guava:jar:15.0:compile checking for updates from"))

        assertTrue(mavenCodeLocationPackager.isDependencyTreeUpdates("         artifact       com.google.guava:guava:         checking for updates"))

        assertTrue(mavenCodeLocationPackager.isDependencyTreeUpdates("      checking for updates   artifact       com.google.guava:guava:      "))

        assertTrue(mavenCodeLocationPackager.isDependencyTreeUpdates("checking for updates"))

        assertFalse(mavenCodeLocationPackager.isDependencyTreeUpdates("com.google.guava:guava:jar:15.0:compile"))

        assertFalse(mavenCodeLocationPackager.isDependencyTreeUpdates("+- com.google.guava:guava:jar:15.0:compile"))

        assertFalse(mavenCodeLocationPackager.isDependencyTreeUpdates("|  \\- com.google.guava:guava:jar:15.0:compile"))

    }

    @Test
    public void testIsGav() {
        MavenCodeLocationPackager mavenCodeLocationPackager = new MavenCodeLocationPackager(null)

        assertFalse(mavenCodeLocationPackager.isGav(" "))

        assertFalse(mavenCodeLocationPackager.isGav("       "))

        assertFalse(mavenCodeLocationPackager.isGav("::::"))

        assertFalse(mavenCodeLocationPackager.isGav(" : : : : "))

        assertFalse(mavenCodeLocationPackager.isGav("group"))

        assertFalse(mavenCodeLocationPackager.isGav("group:artifact"))

        assertFalse(mavenCodeLocationPackager.isGav("group:artifact:version"))

        assertFalse(mavenCodeLocationPackager.isGav("group-artifact:type-classifier-version:scope-garbage"))

        assertFalse(mavenCodeLocationPackager.isGav("group:artifact::classifier:version: :garbage"))

        assertTrue(mavenCodeLocationPackager.isGav("group:artifact:type:version"))

        assertTrue(mavenCodeLocationPackager.isGav("group:artifact:type:classifier:version"))

        assertTrue(mavenCodeLocationPackager.isGav("group:artifact:type:classifier:version:scope"))

        assertTrue(mavenCodeLocationPackager.isGav("group:artifact:type:classifier:version:scope:garbage"))

    }

    @Test
    public void testIndexOfEndOfSegments() {
        MavenCodeLocationPackager mavenCodeLocationPackager = new MavenCodeLocationPackager(null)

        assertEquals(-1, mavenCodeLocationPackager.indexOfEndOfSegments(""))

        assertEquals(-1, mavenCodeLocationPackager.indexOfEndOfSegments("stuff and things"))

        assertEquals(-1, mavenCodeLocationPackager.indexOfEndOfSegments("stuff and things", "things", "and"))

        assertEquals(-1, mavenCodeLocationPackager.indexOfEndOfSegments("stuff and things", "things", "and", "stuff"))

        assertEquals(5, mavenCodeLocationPackager.indexOfEndOfSegments("stuff and things", "stuff"))

        assertEquals(9, mavenCodeLocationPackager.indexOfEndOfSegments("stuff and things", "stuff", "and"))

        assertEquals(16, mavenCodeLocationPackager.indexOfEndOfSegments("stuff and things", "stuff", "and", "things"))

        assertEquals(9, mavenCodeLocationPackager.indexOfEndOfSegments("stuff and things", "and"))

        assertEquals(16, mavenCodeLocationPackager.indexOfEndOfSegments("stuff and things", "things"))
    }

    @Test
    public void testDoesLineContainSegmentsInOrder() {
        MavenCodeLocationPackager mavenCodeLocationPackager = new MavenCodeLocationPackager(null)

        assertFalse(mavenCodeLocationPackager.doesLineContainSegmentsInOrder(""))

        assertFalse(mavenCodeLocationPackager.doesLineContainSegmentsInOrder("stuff and things"))

        assertFalse(mavenCodeLocationPackager.doesLineContainSegmentsInOrder("stuff and things", "things", "and"))

        assertFalse(mavenCodeLocationPackager.doesLineContainSegmentsInOrder("stuff and things", "things", "and", "stuff"))

        assertTrue(mavenCodeLocationPackager.doesLineContainSegmentsInOrder("stuff and things", "stuff"))

        assertTrue(mavenCodeLocationPackager.doesLineContainSegmentsInOrder("stuff and things", "stuff", "and"))

        assertTrue(mavenCodeLocationPackager.doesLineContainSegmentsInOrder("stuff and things", "stuff", "and", "things"))

        assertTrue(mavenCodeLocationPackager.doesLineContainSegmentsInOrder("stuff and things", "and"))

        assertTrue(mavenCodeLocationPackager.doesLineContainSegmentsInOrder("stuff and things", "things"))
    }

    @Test
    public void testLineWithExtraTextAfterScope() {
        MavenCodeLocationPackager mavenCodeLocationPackager = new MavenCodeLocationPackager(new ExternalIdFactory())

        def line = '[INFO] |  |  |  \\- org.eclipse.scout.sdk.deps:org.eclipse.core.jobs:jar:3.8.0.v20160509-0411:compile (version selected from constraint [3.8.0,3.8.1))'
        line = mavenCodeLocationPackager.trimLogLevel(line)
        final String cleanedLine = mavenCodeLocationPackager.calculateCurrentLevelAndCleanLine(line);
        final Dependency dependency = mavenCodeLocationPackager.textToDependency(cleanedLine);
        assertEquals('org.eclipse.scout.sdk.deps:org.eclipse.core.jobs:3.8.0.v20160509-0411', dependency.externalId.createExternalId());
    }

    @Test
    public void testLineWithUnknownScope() {
        MavenCodeLocationPackager mavenCodeLocationPackager = new MavenCodeLocationPackager(new ExternalIdFactory())

        def line = '[INFO] |  |  |  \\- org.eclipse.scout.sdk.deps:org.eclipse.core.jobs:jar:3.8.0.v20160509-0411:pants (version selected from constraint [3.8.0,3.8.1))'
        line = mavenCodeLocationPackager.trimLogLevel(line)
        final String cleanedLine = mavenCodeLocationPackager.calculateCurrentLevelAndCleanLine(line);
        final Dependency dependency = mavenCodeLocationPackager.textToDependency(cleanedLine);
        assertEquals('org.eclipse.scout.sdk.deps:org.eclipse.core.jobs:3.8.0.v20160509-0411', dependency.externalId.createExternalId());
    }

    @Test
    public void testLineWithBadColonPlacement() {
        MavenCodeLocationPackager mavenCodeLocationPackager = new MavenCodeLocationPackager(new ExternalIdFactory())

        def line = '[INFO] |  |  |  \\- org.eclipse.scout.sdk.deps:org.eclipse.core.jobs:jar:3.8.0.v20160509-0411:pants (version selected from: [3.8.0,3.8.1))'
        line = mavenCodeLocationPackager.trimLogLevel(line)
        final String cleanedLine = mavenCodeLocationPackager.calculateCurrentLevelAndCleanLine(line);
        final Dependency dependency = mavenCodeLocationPackager.textToDependency(cleanedLine);
        assertEquals('org.eclipse.scout.sdk.deps:org.eclipse.core.jobs:pants (version selected from', dependency.externalId.createExternalId());
    }


    private void createNewCodeLocationTest(String mavenOutputText, String expectedResourcePath) {
        createNewCodeLocationTest(mavenOutputText, expectedResourcePath, 1, "", "")
    }

    private void createNewCodeLocationTest(String mavenOutputText, String expectedResourcePath, int numberOfCodeLocations, String excludedModules, String includedModules) {
        def mavenCodeLocationPackager = new MavenCodeLocationPackager(new ExternalIdFactory())
        List<MavenParseResult> result = mavenCodeLocationPackager.extractCodeLocations('/test/path', mavenOutputText, excludedModules, includedModules)
        assertEquals(numberOfCodeLocations, result.size())
        DetectCodeLocation codeLocation = result[0].codeLocation
       
        testUtil.testJsonResource(expectedResourcePath, codeLocation)
    }
}
