package com.blackducksoftware.integration.hub.detect.bomtool.packagist

import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory
import com.blackducksoftware.integration.hub.detect.DetectConfiguration
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.packagist.parse.PackagistParser
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation
import com.blackducksoftware.integration.hub.detect.testutils.DependencyGraphResourceTestUtil
import com.blackducksoftware.integration.hub.detect.testutils.TestUtil
import org.junit.Assert
import org.junit.Test

class PackagistTest {
    private TestUtil testUtil = new TestUtil()

    @Test
    public void packagistParserTest() throws IOException {
        final DetectConfiguration detectConfiguration = new DetectConfiguration()
        detectConfiguration.packagistIncludeDevDependencies = true

        final PackagistParser packagistParser = new PackagistParser()
        packagistParser.externalIdFactory = new ExternalIdFactory()
        packagistParser.detectConfiguration = detectConfiguration

        final String composerLockText = testUtil.getResourceAsUTF8String('/packagist/composer.lock')
        final String composerJsonText = testUtil.getResourceAsUTF8String('/packagist/composer.json')
        DetectCodeLocation codeLocation = packagistParser.getDependencyGraphFromProject("source", composerJsonText, composerLockText)

        Assert.assertEquals(codeLocation.bomToolProjectName, "clue/graph-composer");
        Assert.assertEquals(codeLocation.bomToolProjectVersionName, "1.0.0");

        DependencyGraphResourceTestUtil.assertGraph('/packagist/PackagistTestDependencyNode_graph.json', codeLocation.dependencyGraph);
    }
}
