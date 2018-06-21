package com.blackducksoftware.integration.hub.detect.bomtool.packagist

import org.junit.Assert
import org.junit.Test

import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory
import com.blackducksoftware.integration.hub.detect.DetectConfiguration
import com.blackducksoftware.integration.hub.detect.bomtool.packagist.parse.PackagistParseResult
import com.blackducksoftware.integration.hub.detect.bomtool.packagist.parse.PackagistParser
import com.blackducksoftware.integration.hub.detect.testutils.DependencyGraphResourceTestUtil
import com.blackducksoftware.integration.hub.detect.testutils.TestUtil

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
        PackagistParseResult result = packagistParser.getDependencyGraphFromProject("source", composerJsonText, composerLockText)

        Assert.assertEquals(result.projectName, "clue/graph-composer");
        Assert.assertEquals(result.projectVersion, "1.0.0");

        DependencyGraphResourceTestUtil.assertGraph('/packagist/PackagistTestDependencyNode_graph.json', result.codeLocation.dependencyGraph);
    }
}
