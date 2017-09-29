package com.blackducksoftware.integration.hub.detect.bomtool.packagist

import org.junit.Assert
import org.junit.Test

import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory
import com.blackducksoftware.integration.hub.detect.DetectConfiguration
import com.blackducksoftware.integration.hub.detect.DetectProperties
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation
import com.blackducksoftware.integration.hub.detect.testutils.DependencyGraphTestUtil
import com.blackducksoftware.integration.hub.detect.testutils.TestUtil

class PackagistTest {
    private TestUtil testUtil = new TestUtil()

    @Test
    public void packagistParserTest() throws IOException {
        final DetectProperties detectProperties = new DetectProperties()
        detectProperties.packagistIncludeDevDependencies = true
        final DetectConfiguration detectConfiguration = new DetectConfiguration()
        detectConfiguration.detectProperties = detectProperties

        final PackagistParser packagistParser = new PackagistParser()
        packagistParser.externalIdFactory = new ExternalIdFactory()
        packagistParser.detectConfiguration = detectConfiguration

        final String composerLockText = testUtil.getResourceAsUTF8String('/packagist/composer.lock')
        final String composerJsonText = testUtil.getResourceAsUTF8String('/packagist/composer.json')
        DetectCodeLocation codeLocation = packagistParser.getDependencyGraphFromProject("source", composerJsonText, composerLockText)

        Assert.assertEquals(codeLocation.bomToolProjectName, "clue/graph-composer");
        Assert.assertEquals(codeLocation.bomToolProjectVersionName, "1.0.0");

        DependencyGraphTestUtil.assertGraph('/packagist/PackagistTestDependencyNode_graph.json', codeLocation.dependencyGraph);
    }
}
