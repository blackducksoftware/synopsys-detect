package com.blackducksoftware.integration.hub.detect.bomtool.maven

import com.blackducksoftware.integration.hub.bdio.model.dependency.Dependency
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory
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
        assertNull(dependency)
    }

    @Test
    public void testIsLineRelevant() {
        MavenCodeLocationPackager mavenCodeLocationPackager = new MavenCodeLocationPackager(new ExternalIdFactory())

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
        MavenCodeLocationPackager mavenCodeLocationPackager = new MavenCodeLocationPackager(new ExternalIdFactory())

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
        MavenCodeLocationPackager mavenCodeLocationPackager = new MavenCodeLocationPackager(new ExternalIdFactory())

        assertFalse(mavenCodeLocationPackager.isProjectSection(" "))

        assertFalse(mavenCodeLocationPackager.isProjectSection("       "))

        assertFalse(mavenCodeLocationPackager.isProjectSection("---maven-dependency-plugin:"))

        assertFalse(mavenCodeLocationPackager.isProjectSection("---maven-dependency-plugin:other      stuff"))

        assertFalse(mavenCodeLocationPackager.isProjectSection("maven-dependency-plugin:tree      stuff"))

        assertTrue(mavenCodeLocationPackager.isProjectSection("---maven-dependency-plugin:tree      stuff"))

        assertTrue(mavenCodeLocationPackager.isProjectSection("things --- stuff maven-dependency-plugin garbage:tree      stuff"))

        assertTrue(mavenCodeLocationPackager.isProjectSection("things --- stuff maven-dependency-plugin:tree      stuff"))

        assertTrue(mavenCodeLocationPackager.isProjectSection("---maven-dependency-plugin:tree"))
    }

    @Test
    public void testIsGav() {
        MavenCodeLocationPackager mavenCodeLocationPackager = new MavenCodeLocationPackager(new ExternalIdFactory())

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

    private void createNewCodeLocationTest(String mavenOutputText, String expectedResourcePath) {
        createNewCodeLocationTest(mavenOutputText, expectedResourcePath, 1, "", "")
    }

    private void createNewCodeLocationTest(String mavenOutputText, String expectedResourcePath, int numberOfCodeLocations, String excludedModules, String includedModules) {
        def mavenCodeLocationPackager = new MavenCodeLocationPackager(new ExternalIdFactory())
        List<DetectCodeLocation> codeLocations = mavenCodeLocationPackager.extractCodeLocations('/test/path', mavenOutputText, excludedModules, includedModules)
        assertEquals(numberOfCodeLocations, codeLocations.size())
        DetectCodeLocation codeLocation = codeLocations[0]

        testUtil.testJsonResource(expectedResourcePath, codeLocation)
    }
}
