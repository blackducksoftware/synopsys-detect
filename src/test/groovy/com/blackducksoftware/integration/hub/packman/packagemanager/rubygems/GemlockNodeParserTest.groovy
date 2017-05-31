package com.blackducksoftware.integration.hub.packman.packagemanager.rubygems

import java.nio.charset.StandardCharsets

import org.junit.Test

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.NameVersionExternalId
import com.blackducksoftware.integration.hub.packman.testutils.DependencyNodeUtil

class GemlockNodeParserTest {
    DependencyNodeUtil dependencyNodeUtil = new DependencyNodeUtil()

    @Test
    public void testParsingGemfileLock() {
        String gemfileLockContents = getClass().getResourceAsStream('/rubygems/small_gemfile_lock').getText(StandardCharsets.UTF_8.name())
        GemlockNodeParser gemlockNodeParser = new GemlockNodeParser()
        Set<DependencyNode> projectChildren = gemlockNodeParser.parseProjectDependencies(gemfileLockContents)

        DependencyNode root = new DependencyNode('testName', 'testVersion', new NameVersionExternalId(Forge.RUBYGEMS, 'testName', 'testVersion'), projectChildren)
        StringBuilder stringBuilder = new StringBuilder()
        dependencyNodeUtil.buildNodeString(stringBuilder, 0, root)
        println stringBuilder.toString()
    }
}
