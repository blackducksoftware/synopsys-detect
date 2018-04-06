package com.blackducksoftware.integration.hub.detect.bomtool.maven

import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation
import com.blackducksoftware.integration.hub.detect.testutils.TestUtil
import org.junit.Test

import static org.junit.Assert.assertEquals

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

    private void createNewCodeLocationTest(String mavenOutputText, String expectedResourcePath) {
        createNewCodeLocationTest(mavenOutputText, expectedResourcePath, 1, "", "")
    }

    private void createNewCodeLocationTest(String mavenOutputText, String expectedResourcePath, int numberOfCodeLocations, String excludedModules, String includedModules) {
        def mavenCodeLocationPackager = new MavenCodeLocationPackager()
        mavenCodeLocationPackager.externalIdFactory = new ExternalIdFactory()
        List<DetectCodeLocation> codeLocations = mavenCodeLocationPackager.extractCodeLocations('/test/path', mavenOutputText, excludedModules, includedModules)
        assertEquals(numberOfCodeLocations, codeLocations.size())
        DetectCodeLocation codeLocation = codeLocations[0]

        testUtil.testJsonResource(expectedResourcePath, codeLocation)
    }
}
