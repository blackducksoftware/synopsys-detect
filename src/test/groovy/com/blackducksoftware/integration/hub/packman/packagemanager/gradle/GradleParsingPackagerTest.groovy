package com.blackducksoftware.integration.hub.packman.packagemanager.gradle

import java.nio.charset.StandardCharsets

import org.junit.Test

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.ExternalId
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.MavenExternalId
import com.google.gson.Gson
import com.google.gson.GsonBuilder

class GradleParsingPackagerTest {
    @Test
    public void testMultipleConfigurations() {
        InputStream dependenciesInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream('gradle/gradle-dependencies-hub-artifactory')
        String dependenciesContent = dependenciesInputStream.getText(StandardCharsets.UTF_8.name())

        InputStream expectedInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream('gradle/gradle-dependencies-hub-artifactory-expected')
        String expectedContent = expectedInputStream.getText(StandardCharsets.UTF_8.name())

        GradleParsingPackager gradleParsingPackager = new GradleParsingPackager(null, null, null)
        def rootProjectNode = new DependencyNode('project', 'version', new MavenExternalId('group', 'project', 'version'))
        gradleParsingPackager.createDependencyNodesFromOutputLines(rootProjectNode, dependenciesContent.split('\n'))

        GsonBuilder gsonBuilder = new GsonBuilder()
        gsonBuilder.registerTypeAdapter(ExternalId.class, new ExternalIdTypeAdapter())
        gsonBuilder.setPrettyPrinting()
        Gson gson = gsonBuilder.create()
        File jsonTest = new File('/Users/ekerwin/Documents/jsonTest.json')
        jsonTest.delete()
        jsonTest << gson.toJson(rootProjectNode)
        String jsonContent = jsonTest.getText(StandardCharsets.UTF_8.name())
        DependencyNode fromJson = gson.fromJson(jsonContent, DependencyNode.class)

        StringBuilder stringBuilder = new StringBuilder()
        buildNodeString(stringBuilder, 0, rootProjectNode)
        println(stringBuilder.toString())

        stringBuilder = new StringBuilder()
        buildNodeString(stringBuilder, 0, fromJson)
        println(stringBuilder.toString())
    }

    def buildNodeString(StringBuilder stringBuilder, int currentLevel, DependencyNode node) {
        String prefix = '  '.multiply(currentLevel)
        stringBuilder.append(prefix + node.externalId.createExternalId() + '\n')
        node.children.each {
            buildNodeString(stringBuilder, currentLevel + 1, it)
        }
    }

    Comparator<DependencyNode> nodeComparator() {
        new Comparator<DependencyNode>() {
                    public int compare(final DependencyNode lhs, final DependencyNode rhs) {
                        if (lhs == rhs || lhs.externalId == rhs.externalId) {
                            0
                        } else if (lhs == null || lhs.externalId == null) {
                            -1
                        } else if (rhs == null || rhs.externalId == null) {
                            1
                        }

                        lhs.externalId.createDataId().compareTo(rhs.externalId.createDataId())
                    }
                }
    }
}