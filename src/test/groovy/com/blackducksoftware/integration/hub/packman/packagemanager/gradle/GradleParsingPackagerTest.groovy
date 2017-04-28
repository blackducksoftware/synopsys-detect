package com.blackducksoftware.integration.hub.packman.packagemanager.gradle

import java.nio.charset.StandardCharsets

import org.junit.Assert
import org.junit.Test

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.ExternalId
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.MavenExternalId
import com.blackducksoftware.integration.hub.packman.testutils.DependencyNodeUtil
import com.blackducksoftware.integration.hub.packman.testutils.ExternalIdTypeAdapter
import com.google.gson.Gson
import com.google.gson.GsonBuilder

class GradleParsingPackagerTest {
    DependencyNodeUtil dependencyNodeUtil = new DependencyNodeUtil()

    @Test
    public void testParsingStandardLines() {
        GradleParsingPackager gradleParsingPackager = new GradleParsingPackager(null, null, null)
        String outputLine = '|    |    +--- com.google.code.gson:gson:2.7'
        DependencyNode node = gradleParsingPackager.createDependencyNodeFromOutputLine(outputLine)
        Assert.assertEquals('gson', node.name)
        Assert.assertEquals('2.7', node.version)
        Assert.assertEquals('com.google.code.gson:gson:2.7', node.externalId.createExternalId())
        Assert.assertEquals(Forge.maven, node.externalId.forge)
        Assert.assertTrue(node.children.empty)
    }

    @Test
    public void testWinningVersionLines() {
        GradleParsingPackager gradleParsingPackager = new GradleParsingPackager(null, null, null)
        String outputLine = '|    |    |    \\--- org.slf4j:slf4j-api:1.7.21 -> 1.7.22'
        DependencyNode node = gradleParsingPackager.createDependencyNodeFromOutputLine(outputLine)
        Assert.assertEquals('slf4j-api', node.name)
        Assert.assertEquals('1.7.22', node.version)
        Assert.assertEquals('org.slf4j:slf4j-api:1.7.22', node.externalId.createExternalId())
        Assert.assertEquals(Forge.maven, node.externalId.forge)
        Assert.assertTrue(node.children.empty)
    }

    @Test
    public void testParsingSeenElsewhereLines() {
        GradleParsingPackager gradleParsingPackager = new GradleParsingPackager(null, null, null)
        String outputLine = '|    |         \\--- com.squareup.okhttp3:okhttp:3.4.2 (*)'
        DependencyNode standardDependencyNode = gradleParsingPackager.createDependencyNodeFromOutputLine(outputLine)
        Assert.assertEquals('okhttp', standardDependencyNode.name)
        Assert.assertEquals('3.4.2', standardDependencyNode.version)
        Assert.assertEquals('com.squareup.okhttp3:okhttp:3.4.2', standardDependencyNode.externalId.createExternalId())
        Assert.assertEquals(Forge.maven, standardDependencyNode.externalId.forge)
        Assert.assertTrue(standardDependencyNode.children.empty)
    }

    @Test
    public void testMultipleConfigurations() {
        InputStream dependenciesInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream('gradle/gradle-dependencies-hub-artifactory')
        String dependenciesContent = dependenciesInputStream.getText(StandardCharsets.UTF_8.name())

        GradleParsingPackager gradleParsingPackager = new GradleParsingPackager(null, null, null)
        def actualDependencyNode = new DependencyNode('project', 'version', new MavenExternalId('group', 'project', 'version'))
        gradleParsingPackager.createDependencyNodesFromOutputLines(actualDependencyNode, dependenciesContent.split('\n'))

        InputStream expectedInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream('gradle/gradle-dependencies-hub-artifactory-expected.json')
        String expectedJson = expectedInputStream.getText(StandardCharsets.UTF_8.name())

        Gson gson = new GsonBuilder().registerTypeAdapter(ExternalId.class, new ExternalIdTypeAdapter()).create()
        def expectedDependencyNode = gson.fromJson(expectedJson, DependencyNode.class)

        dependencyNodeUtil.sortDependencyNode(expectedDependencyNode)
        dependencyNodeUtil.sortDependencyNode(actualDependencyNode)

        StringBuilder expectedStringBuilder = new StringBuilder()
        dependencyNodeUtil.buildNodeString(expectedStringBuilder, 0, expectedDependencyNode)

        StringBuilder actualStringBuilder = new StringBuilder()
        dependencyNodeUtil.buildNodeString(actualStringBuilder, 0, actualDependencyNode)

        Assert.assertEquals(expectedStringBuilder.toString(), actualStringBuilder.toString())
    }
}
