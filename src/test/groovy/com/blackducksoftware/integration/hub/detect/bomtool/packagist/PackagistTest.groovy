package com.blackducksoftware.integration.hub.detect.bomtool.packagist

import org.junit.Test

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.ExternalIdFactory
import com.blackducksoftware.integration.hub.detect.DetectConfiguration
import com.blackducksoftware.integration.hub.detect.DetectProperties
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
        DependencyNode dependencyNode = packagistParser.getDependencyNodeFromProject(composerJsonText, composerLockText)

        testUtil.testJsonResource('/packagist/PackagistTestDependencyNode.txt', dependencyNode)
    }
}
