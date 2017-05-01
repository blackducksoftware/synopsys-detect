package com.blackducksoftware.integration.hub.packman.packagemanager.gradle

import java.nio.charset.StandardCharsets

import org.junit.Assert
import org.junit.Test

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge

class PropertiesParserTest {
    @Test
    public void testCreatingDependencyNodeFromProperties() {
        InputStream propertiesInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream('gradle/hub-rest-backend-properties')
        String propertiesContent = propertiesInputStream.getText(StandardCharsets.UTF_8.name())

        PropertiesParser propertiesParser = new PropertiesParser()
        DependencyNode dependencyNode = propertiesParser.createProjectDependencyNodeFromProperties(propertiesContent)
        Assert.assertEquals("rest-backend", dependencyNode.name)
        Assert.assertEquals("3.7.0-SNAPSHOT", dependencyNode.version)
        Assert.assertTrue(dependencyNode.children.empty)
        Assert.assertEquals(Forge.maven, dependencyNode.externalId.forge)
        Assert.assertEquals("com.blackducksoftware.hub", dependencyNode.externalId.group)
        Assert.assertEquals("rest-backend", dependencyNode.externalId.name)
        Assert.assertEquals("3.7.0-SNAPSHOT", dependencyNode.externalId.version)
    }
}