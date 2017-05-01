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
import com.blackducksoftware.integration.hub.packman.util.ExcludedIncludedFilter
import com.google.gson.Gson
import com.google.gson.GsonBuilder

class DependenciesParserTest {
    static final ExcludedIncludedFilter FILTER_NOTHING = new ExcludedIncludedFilter("", "")
    static final DependencyNodeUtil DEPENDENCY_NODE_UTIL = new DependencyNodeUtil()

    @Test
    public void testParsingStandardLines() {
        DependenciesParser dependenciesParser = new DependenciesParser()
        String outputLine = '|    |    +--- com.google.code.gson:gson:2.7'
        DependencyNode node = dependenciesParser.createDependencyNodeFromOutputLine(outputLine)
        Assert.assertEquals('gson', node.name)
        Assert.assertEquals('2.7', node.version)
        Assert.assertEquals('com.google.code.gson:gson:2.7', node.externalId.createExternalId())
        Assert.assertEquals(Forge.maven, node.externalId.forge)
        Assert.assertTrue(node.children.empty)
    }

    @Test
    public void testWinningVersionLines() {
        DependenciesParser dependenciesParser = new DependenciesParser()
        String outputLine = '|    |    |    \\--- org.slf4j:slf4j-api:1.7.21 -> 1.7.22'
        DependencyNode node = dependenciesParser.createDependencyNodeFromOutputLine(outputLine)
        Assert.assertEquals('slf4j-api', node.name)
        Assert.assertEquals('1.7.22', node.version)
        Assert.assertEquals('org.slf4j:slf4j-api:1.7.22', node.externalId.createExternalId())
        Assert.assertEquals(Forge.maven, node.externalId.forge)
        Assert.assertTrue(node.children.empty)
    }

    @Test
    public void testParsingSeenElsewhereLines() {
        DependenciesParser dependenciesParser = new DependenciesParser()
        String outputLine = '|    |         \\--- com.squareup.okhttp3:okhttp:3.4.2 (*)'
        DependencyNode standardDependencyNode = dependenciesParser.createDependencyNodeFromOutputLine(outputLine)
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

        DependenciesParser dependenciesParser = new DependenciesParser()
        def actualDependencyNode = new DependencyNode('project', 'version', new MavenExternalId('group', 'project', 'version'))
        Assert.assertTrue(actualDependencyNode.children.empty)
        dependenciesParser.populateDependencyNodeFromDependencies(actualDependencyNode, dependenciesContent, FILTER_NOTHING)
        Assert.assertFalse(actualDependencyNode.children.empty)

        InputStream expectedInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream('gradle/gradle-dependencies-hub-artifactory-expected.json')
        String expectedJson = expectedInputStream.getText(StandardCharsets.UTF_8.name())

        Gson gson = new GsonBuilder().registerTypeAdapter(ExternalId.class, new ExternalIdTypeAdapter()).create()
        def expectedDependencyNode = gson.fromJson(expectedJson, DependencyNode.class)

        DEPENDENCY_NODE_UTIL.sortDependencyNode(expectedDependencyNode)
        DEPENDENCY_NODE_UTIL.sortDependencyNode(actualDependencyNode)

        StringBuilder expectedStringBuilder = new StringBuilder()
        DEPENDENCY_NODE_UTIL.buildNodeString(expectedStringBuilder, 0, expectedDependencyNode)

        StringBuilder actualStringBuilder = new StringBuilder()
        DEPENDENCY_NODE_UTIL.buildNodeString(actualStringBuilder, 0, actualDependencyNode)

        Assert.assertEquals(expectedStringBuilder.toString(), actualStringBuilder.toString())
    }

    @Test
    public void testFilteringConfigurations() {
        InputStream dependenciesInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream('gradle/gradle-dependencies-hub-artifactory')
        String dependenciesContent = dependenciesInputStream.getText(StandardCharsets.UTF_8.name())

        DependenciesParser dependenciesParser = new DependenciesParser()
        def actualDependencyNode = new DependencyNode('project', 'version', new MavenExternalId('group', 'project', 'version'))
        Assert.assertTrue(actualDependencyNode.children.empty)
        dependenciesParser.populateDependencyNodeFromDependencies(actualDependencyNode, dependenciesContent, new ExcludedIncludedFilter("", "nonsense"))
        Assert.assertTrue(actualDependencyNode.children.empty)
    }
}
