package com.blackducksoftware.integration.hub.packman.parser.gradle

import org.junit.Assert
import org.junit.Test

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.MavenExternalId

class GradlePackagerTest {
    @Test
    public void testGradlePackager() {
        def sourcePath = '/Users/ekerwin/Documents/source/rest-backend/registration/registration.core'
        def gradlePackager = new GradlePackager(sourcePath)
        def dependencyNodes = gradlePackager.makeDependencyNodes()
        dependencyNodes.each { println "${it.name}/${it.version}: ${it.externalId.createDataId()}: ${it.externalId.createExternalId()}" }
    }

    @Test
    public void testParsingStandardLines() {
        def gradlePackager = new GradlePackager(null)
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
        def gradlePackager = new GradlePackager(null)
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
        def gradlePackager = new GradlePackager(null)
        String outputLine = '|    |         \\--- com.squareup.okhttp3:okhttp:3.4.2 (*)'
        DependencyNode standardDependencyNode = gradlePackager.createDependencyNodeFromOutputLine(outputLine)
        Assert.assertEquals('okhttp', standardDependencyNode.name)
        Assert.assertEquals('3.4.2', standardDependencyNode.version)
        Assert.assertEquals('com.squareup.okhttp3:okhttp:3.4.2', standardDependencyNode.externalId.createExternalId())
        Assert.assertEquals(Forge.maven, standardDependencyNode.externalId.forge)
        Assert.assertTrue(standardDependencyNode.children.empty)
    }

    @Test
    public void showEnv() {
        System.getenv().each { key, value ->
            println "${key} :: ${value}"
        }
    }

    @Test
    public void printMaven() {
        String output = 'which mvn'.execute(null, new File('/Users/ekerwin/Documents/source/integration/hub-artifactory')).text
        println output
    }

    @Test
    public void testGradleOutput() {
        def gradlePackager = new GradlePackager('/Users/ekerwin/Documents/source/integration/hub-artifactory')
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

    private String getTestOutput() {
        """
+--- com.blackducksoftware.integration:hub-common:11.0.0-SNAPSHOT
|    +--- com.blackducksoftware.integration:hub-common-rest:1.0.0
|    |    +--- com.blackducksoftware.integration:integration-common:5.2.1
|    |    |    +--- org.apache.commons:commons-lang3:3.5
|    |    |    +--- commons-io:commons-io:2.5
|    |    |    +--- commons-codec:commons-codec:1.10
|    |    |    \\--- org.slf4j:slf4j-api:1.7.21 -> 1.7.22
|    |    +--- com.google.code.gson:gson:2.7
|    |    +--- com.squareup.okhttp3:okhttp:3.4.2
|    |    |    \\--- com.squareup.okio:okio:1.9.0
|    |    \\--- com.squareup.okhttp3:okhttp-urlconnection:3.4.2
|    |         \\--- com.squareup.okhttp3:okhttp:3.4.2 (*)
|    +--- com.blackducksoftware.integration:hub-common-response:0.0.6
|    |    +--- org.apache.commons:commons-lang3:3.5
|    |    \\--- com.google.code.gson:gson:2.7
|    +--- com.blackducksoftware.integration:hub-common-reporting:1.0.1
|    |    +--- org.apache.commons:commons-lang3:3.4 -> 3.5
|    |    +--- commons-io:commons-io:2.5
|    |    \\--- com.google.code.gson:gson:2.7
|    +--- com.blackducksoftware.integration:phone-home-api:1.5.1
|    |    +--- commons-codec:commons-codec:1.10
|    |    +--- com.google.code.gson:gson:2.7
|    |    +--- com.squareup.okhttp3:okhttp:3.4.2 (*)
|    |    +--- com.squareup.okhttp3:okhttp-urlconnection:3.4.2 (*)
|    |    \\--- org.apache.commons:commons-lang3:3.4 -> 3.5
|    +--- com.blackducksoftware.integration:integration-common:5.2.1 (*)
|    +--- com.blackducksoftware.integration:integration-bdio:2.0.0-SNAPSHOT
|    |    +--- org.apache.commons:commons-lang3:3.5
|    |    \\--- com.google.code.gson:gson:2.7
|    +--- joda-time:joda-time:2.9.6
|    +--- com.google.code.gson:gson:2.7
|    +--- com.squareup.okhttp3:okhttp:3.4.2 (*)
|    \\--- com.squareup.okhttp3:okhttp-urlconnection:3.4.2 (*)
+--- org.springframework.boot:spring-boot-starter: -> 1.4.3.RELEASE
|    +--- org.springframework.boot:spring-boot:1.4.3.RELEASE
|    |    +--- org.springframework:spring-core:4.3.5.RELEASE
|    |    \\--- org.springframework:spring-context:4.3.5.RELEASE
|    |         +--- org.springframework:spring-aop:4.3.5.RELEASE
|    |         |    +--- org.springframework:spring-beans:4.3.5.RELEASE
|    |         |    |    \\--- org.springframework:spring-core:4.3.5.RELEASE
|    |         |    \\--- org.springframework:spring-core:4.3.5.RELEASE
|    |         +--- org.springframework:spring-beans:4.3.5.RELEASE (*)
|    |         +--- org.springframework:spring-core:4.3.5.RELEASE
|    |         \\--- org.springframework:spring-expression:4.3.5.RELEASE
|    |              \\--- org.springframework:spring-core:4.3.5.RELEASE
|    +--- org.springframework.boot:spring-boot-autoconfigure:1.4.3.RELEASE
|    |    \\--- org.springframework.boot:spring-boot:1.4.3.RELEASE (*)
|    +--- org.springframework.boot:spring-boot-starter-logging:1.4.3.RELEASE
|    |    +--- ch.qos.logback:logback-classic:1.1.8
|    |    |    +--- ch.qos.logback:logback-core:1.1.8
|    |    |    \\--- org.slf4j:slf4j-api:1.7.21 -> 1.7.22
|    |    +--- org.slf4j:jcl-over-slf4j:1.7.22
|    |    |    \\--- org.slf4j:slf4j-api:1.7.22
|    |    +--- org.slf4j:jul-to-slf4j:1.7.22
|    |    |    \\--- org.slf4j:slf4j-api:1.7.22
|    |    \\--- org.slf4j:log4j-over-slf4j:1.7.22
|    |         \\--- org.slf4j:slf4j-api:1.7.22
|    +--- org.springframework:spring-core:4.3.5.RELEASE
|    \\--- org.yaml:snakeyaml:1.17
+--- org.springframework:spring-web: -> 4.3.5.RELEASE
|    +--- org.springframework:spring-aop:4.3.5.RELEASE (*)
|    +--- org.springframework:spring-beans:4.3.5.RELEASE (*)
|    +--- org.springframework:spring-context:4.3.5.RELEASE (*)
|    \\--- org.springframework:spring-core:4.3.5.RELEASE
+--- org.codehaus.groovy:groovy-all:2.4.7
\\--- org.apache.commons:commons-compress:1.13
"""
    }
}