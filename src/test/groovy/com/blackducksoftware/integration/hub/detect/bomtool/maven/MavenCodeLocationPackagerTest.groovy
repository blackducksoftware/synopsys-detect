package com.blackducksoftware.integration.hub.detect.bomtool.maven

import static org.junit.Assert.*

import org.junit.Test

import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.MavenExternalId
import com.blackducksoftware.integration.hub.detect.model.BomToolType
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation
import com.blackducksoftware.integration.hub.detect.testutils.TestUtil

class MavenCodeLocationPackagerTest {
    private TestUtil testUtil = new TestUtil()

    @Test
    public void extractCodeLocationsTest() {
        final String mavenOutputText = testUtil.getResourceAsUTF8String('/maven/sonarStashOutput.txt')
        createNewCodeLocationTest(mavenOutputText)
    }

    @Test
    public void extractCodeLocationsCorruptTest() {
        final String mavenOutputText = testUtil.getResourceAsUTF8String('/maven/sonarStashCorruptOutput.txt')
        createNewCodeLocationTest(mavenOutputText)
    }

    private void createNewCodeLocationTest(String mavenOutputText) {
        def mavenCodeLocationPackager = new MavenCodeLocationPackager()
        List<DetectCodeLocation> codeLocations = mavenCodeLocationPackager.extractCodeLocations('/test/path', mavenOutputText)
        assertEquals(1, codeLocations.size())
        DetectCodeLocation codeLocation = codeLocations[0]

        testUtil.testJsonResource('/maven/sonarStashCodeLocation.json', codeLocation)
    }
}
