package com.blackducksoftware.integration.hub.packman.parser.gradle

import org.junit.Assert
import org.junit.Ignore
import org.junit.Test

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.MavenExternalId
import com.blackducksoftware.integration.hub.packman.packagemanager.gradle.GradleParsingPackager

@Ignore
class GradleParsingPackagerTest {
    @Test
    public void testParsingStandardLines() {
        def gradlePackager = new GradleParsingPackager(null)
        String outputLine = '|    |    +--- com.google.code.gson:gson:2.7'
        DependencyNode node = gradlePackager.createDependencyNodeFromOutputLine(outputLine)
        Assert.assertEquals('gson', node.name)
        Assert.assertEquals('2.7', node.version)
        Assert.assertEquals('com.google.code.gson:gson:2.7', node.externalId.createExternalId())
        Assert.assertEquals(Forge.maven, node.externalId.forge)
        Assert.assertTrue(node.children.empty)
    }

    @Test
    public void testWinningVersionLines() {
        def gradlePackager = new GradleParsingPackager(null)
        String outputLine = '|    |    |    \\--- org.slf4j:slf4j-api:1.7.21 -> 1.7.22'
        DependencyNode node = gradlePackager.createDependencyNodeFromOutputLine(outputLine)
        Assert.assertEquals('slf4j-api', node.name)
        Assert.assertEquals('1.7.22', node.version)
        Assert.assertEquals('org.slf4j:slf4j-api:1.7.22', node.externalId.createExternalId())
        Assert.assertEquals(Forge.maven, node.externalId.forge)
        Assert.assertTrue(node.children.empty)
    }

    @Test
    public void testParsingSeenElsewhereLines() {
        def gradlePackager = new GradleParsingPackager(null)
        String outputLine = '|    |         \\--- com.squareup.okhttp3:okhttp:3.4.2 (*)'
        DependencyNode standardDependencyNode = gradlePackager.createDependencyNodeFromOutputLine(outputLine)
        Assert.assertEquals('okhttp', standardDependencyNode.name)
        Assert.assertEquals('3.4.2', standardDependencyNode.version)
        Assert.assertEquals('com.squareup.okhttp3:okhttp:3.4.2', standardDependencyNode.externalId.createExternalId())
        Assert.assertEquals(Forge.maven, standardDependencyNode.externalId.forge)
        Assert.assertTrue(standardDependencyNode.children.empty)
    }

    @Test
    public void testGradleOutput() {
        def gradlePackager = new GradleParsingPackager('/Users/ekerwin/Documents/source/integration/hub-artifactory')
        DependencyNode root = new DependencyNode('project', 'version', new MavenExternalId(Forge.maven, 'root', 'project', 'version'))
        List<DependencyNode> nodes = gradlePackager.createDependencyNodesFromOutputLines(root, Arrays.asList(getTestOutput().split('\n')))
        printNodes(0, root)
    }

    def printNodes(int currentLevel, DependencyNode node) {
        String prefix = '  '.multiply(currentLevel)
        println prefix + node.externalId.createExternalId()
        node.children.each {
            printNodes(currentLevel + 1, it)
        }
    }
}