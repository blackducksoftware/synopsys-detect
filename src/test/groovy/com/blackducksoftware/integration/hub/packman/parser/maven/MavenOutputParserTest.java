/*
 * Copyright (C) 2017 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Black Duck Software ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Black Duck Software.
 */
package com.blackducksoftware.integration.hub.packman.parser.maven;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode;
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge;
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.ExternalId;
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.MavenExternalId;
import com.blackducksoftware.integration.hub.packman.packagemanager.maven.parsers.MavenOutputParser;

public class MavenOutputParserTest {

    @Test
    public void mavenParserTest() throws IOException {
        final MavenOutputParser mavenOutputParser = new MavenOutputParser(null);
        final String mavenOutput = IOUtils.toString(getClass().getResourceAsStream("/maven/mavenSampleOutput.txt"), StandardCharsets.UTF_8);
        final List<DependencyNode> projects = mavenOutputParser.parse(mavenOutput);

        assertEquals(1, projects.size());
        assertMavenDependencyNodesEqual(getIntegationBdioDependencyNode(), projects.get(0));
    }

    @Test
    public void mavenParserScopeTest() throws IOException {
        final List<String> scopes = new ArrayList<>();
        scopes.add("compile");
        scopes.add("provided");
        final MavenOutputParser mavenOutputParser = new MavenOutputParser(scopes);
        final String mavenOutput = IOUtils.toString(getClass().getResourceAsStream("/maven/mavenSampleOutput.txt"), StandardCharsets.UTF_8);
        final List<DependencyNode> projects = mavenOutputParser.parse(mavenOutput);

        assertEquals(1, projects.size());
        assertMavenDependencyNodesEqual(getScopedIntegationBdioDependencyNode(), projects.get(0));
    }

    @Test
    public void mavenParserAllScopesTest() throws IOException {
        final List<String> scopes = new ArrayList<>();
        scopes.add("compile");
        scopes.add("all");
        final MavenOutputParser mavenOutputParser = new MavenOutputParser(scopes);
        final String mavenOutput = IOUtils.toString(getClass().getResourceAsStream("/maven/mavenSampleOutput.txt"), StandardCharsets.UTF_8);
        final List<DependencyNode> projects = mavenOutputParser.parse(mavenOutput);

        assertEquals(1, projects.size());
        assertMavenDependencyNodesEqual(getIntegationBdioDependencyNode(), projects.get(0));
    }

    private DependencyNode getScopedIntegationBdioDependencyNode() {
        final DependencyNode project = createMavenNode("com.blackducksoftware.integration", "integration-bdio", "2.0.0-SNAPSHOT");
        final DependencyNode gson = createMavenNode("com.google.code.gson", "gson", "2.7");
        final DependencyNode commonslang = createMavenNode("org.apache.commons", "commons-lang3", "3.5");
        project.children.add(commonslang);
        project.children.add(gson);
        return project;
    }

    private DependencyNode getIntegationBdioDependencyNode() {
        final DependencyNode project = createMavenNode("com.blackducksoftware.integration", "integration-bdio", "2.0.0-SNAPSHOT");
        final DependencyNode junit = createMavenNode("junit", "junit", "4.12");
        final DependencyNode jsonassert = createMavenNode("org.skyscreamer", "jsonassert", "1.4.0");
        final DependencyNode commonslang = createMavenNode("org.apache.commons", "commons-lang3", "3.5");
        final DependencyNode gson = createMavenNode("com.google.code.gson", "gson", "2.7");
        final DependencyNode adnroidJson = createMavenNode("com.vaadin.external.google", "android-json", "0.0.20131108.vaadin1");
        final DependencyNode hamcrestCore = createMavenNode("org.hamcrest", "hamcrest-core", "1.3");
        final DependencyNode groovyAll = createMavenNode("org.codehaus.groovy", "groovy-all", "2.4.8");
        final DependencyNode mokitoAll = createMavenNode("org.mockito", "mockito-all", "1.10.19");
        final DependencyNode powermockApiMockito = createMavenNode("org.powermock", "powermock-api-mockito", "1.6.6");
        final DependencyNode mokitoCore = createMavenNode("org.mockito", "mockito-core", "1.10.19");
        final DependencyNode objenesis = createMavenNode("org.objenesis", "objenesis", "2.1");
        final DependencyNode powermockApiMockitoCommon = createMavenNode("org.powermock", "powermock-api-mockito-common", "1.6.6");
        final DependencyNode powermockApiSupport = createMavenNode("org.powermock", "powermock-api-support", "1.6.6");
        final DependencyNode powermockModuleJunit = createMavenNode("org.powermock", "powermock-module-junit4", "1.6.6");
        final DependencyNode powermockModuleJunitCommon = createMavenNode("org.powermock", "powermock-module-junit4-common", "1.6.6");
        final DependencyNode powermockCore = createMavenNode("org.powermock", "powermock-core", "1.6.6");
        final DependencyNode powermockReflect = createMavenNode("org.powermock", "powermock-reflect", "1.6.6");
        final DependencyNode javassist = createMavenNode("org.javassist", "javassist", "3.21.0-GA");

        jsonassert.children.add(adnroidJson);
        junit.children.add(hamcrestCore);
        powermockApiMockito.children.add(mokitoCore);
        powermockApiMockito.children.add(powermockApiMockitoCommon);
        mokitoCore.children.add(objenesis);
        powermockApiMockitoCommon.children.add(powermockApiSupport);
        powermockModuleJunit.children.add(powermockModuleJunitCommon);
        powermockModuleJunitCommon.children.add(powermockCore);
        powermockModuleJunitCommon.children.add(powermockReflect);
        powermockCore.children.add(javassist);

        project.children.add(commonslang);
        project.children.add(gson);
        project.children.add(jsonassert);
        project.children.add(junit);
        project.children.add(groovyAll);
        project.children.add(mokitoAll);
        project.children.add(powermockApiMockito);
        project.children.add(powermockModuleJunit);

        return project;
    }

    private DependencyNode createMavenNode(final String group, final String artifact, final String version) {
        final ExternalId externalId = new MavenExternalId(Forge.maven, group, artifact, version);
        final DependencyNode node = new DependencyNode(artifact, version, externalId);
        return node;
    }

    private void assertMavenDependencyNodesEqual(final DependencyNode expected, final DependencyNode actual) {
        final String message = String.format("Assertion failure comparing these two nodes:\n%s\nand\n%s\n", expected, actual);
        assertEquals(message, expected.name, actual.name);
        assertEquals(message, expected.version, actual.version);
        assertEquals(message, expected.externalId.forge, actual.externalId.forge);
        assertEquals(message, expected.externalId.createExternalId(), actual.externalId.createExternalId());
        assertEquals(String.format("Number of children don't match. %s", message), expected.children.size(), actual.children.size());
        for (final DependencyNode expectedNode : expected.children) {
            boolean foundMatch = false;
            for (final DependencyNode actualNode : actual.children) {
                if (actualNode.name.equals(expectedNode.name)) {
                    foundMatch = true;
                    assertMavenDependencyNodesEqual(expectedNode, actualNode);
                    break;
                }
            }
            assertEquals(String.format("Expected node not found:\n%s", expectedNode), true, foundMatch);
        }
    }
}
