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
package com.blackducksoftware.integration.hub.detect.bomtool.gradle

import static org.junit.Assert.*

import org.junit.Test
import org.springframework.test.util.ReflectionTestUtils

import com.blackducksoftware.integration.hub.bdio.model.dependency.Dependency
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation
import com.blackducksoftware.integration.hub.detect.model.DetectProject
import com.blackducksoftware.integration.hub.detect.testutils.TestUtil
import com.blackducksoftware.integration.util.ResourceUtil
import com.google.gson.GsonBuilder

class GradleDependenciesParserTest {
    private TestUtil testUtil = new TestUtil()
    private ExternalIdFactory externalIdFactory = new ExternalIdFactory()

    @Test
    public void getLineLevelTest() {
        GradleDependenciesParser gradleDependenciesParser = new GradleDependenciesParser()
        assertEquals(5, gradleDependenciesParser.getLineLevel('|    |         |    |    \\--- org.springframework:spring-core:4.3.5.RELEASE'))
        assertEquals(3, gradleDependenciesParser.getLineLevel('|    |         \\--- com.squareup.okhttp3:okhttp:3.4.2 (*)'))
        assertEquals(4, gradleDependenciesParser.getLineLevel('     |    |         \\--- org.ow2.asm:asm:5.0.3'))
        assertEquals(1, gradleDependenciesParser.getLineLevel('     +--- org.hamcrest:hamcrest-core:1.3'))
        assertEquals(0, gradleDependenciesParser.getLineLevel('+--- org.springframework.boot:spring-boot-starter: -> 1.4.3.RELEASE'))
        assertEquals(0, gradleDependenciesParser.getLineLevel('\\--- org.apache.commons:commons-compress:1.13'))
    }

    @Test
    public void extractCodeLocationTest() {
        createNewCodeLocationTest('gradle/dependencyGraph.txt', '/gradle/dependencyGraph-expected.json', "hub-detect", "2.0.0-SNAPSHOT")
    }

    @Test
    public void complexTest() {
        DetectCodeLocation codeLocation = build('gradle/parse-tests/complex_dependencyGraph.txt');
        assertHas(codeLocation, "non-project:with-nested:1.0.0");
        assertHas(codeLocation, "solo:component:4.12");
        assertHas(codeLocation, "some.group:child:2.2.2");
        assertHas(codeLocation, "terminal:child:6.2.3");

        assertDoesNotHave(codeLocation, "child-project");
        assertDoesNotHave(codeLocation, "nested-parent");
        assertDoesNotHave(codeLocation, "spring-webflux");
        assertDoesNotHave(codeLocation, "spring-beans");
        assertDoesNotHave(codeLocation, "spring-core");
        assertDoesNotHave(codeLocation, "spring-web");
        assertDoesNotHave(codeLocation, "should-suppress");
    }

    private void assertDoesNotHave(DetectCodeLocation codeLocation, String name) {
        assertDoesNotHave(codeLocation, name, null);
    }

    private void assertDoesNotHave(DetectCodeLocation codeLocation, String name, ExternalId current) {
        if (current == null){
            for (Dependency dep : codeLocation.dependencyGraph.getRootDependencies()){
                assertDoesNotHave(dep, name);
                assertDoesNotHave(codeLocation, name, dep.externalId);
            }
        }else{
            for (Dependency dep : codeLocation.dependencyGraph.getChildrenForParent(current)){
                assertDoesNotHave(dep, name);
                assertDoesNotHave(codeLocation, name, dep.externalId);
            }
        }
    }

    private void assertDoesNotHave(Dependency dep, String name) {
        assertFalse("Dependency name contains '" + name + "'", dep.name.contains(name));
        assertFalse("Dependency version contains '" + name + "'", dep.version.contains(name));
        assertFalse("External id version contains '" + name + "'", dep.externalId.version.contains(name));
        assertFalse("External id group contains '" + name + "'", dep.externalId.group.contains(name));
        assertFalse("External id name contains '" + name + "'", dep.externalId.name.contains(name));
    }

    private void assertHas(DetectCodeLocation codeLocation, String gav) {
        String[] split = gav.split(":");
        assertHas(codeLocation, split[0], split[1], split[2]);
    }

    private void assertHas(DetectCodeLocation codeLocation, String org, String name, String version) {

        Dependency dep = codeLocation.dependencyGraph.getDependency(externalIdFactory.createMavenExternalId(org, name, version))
        assertNotNull(dep);
    }

    private DetectCodeLocation build(String resource){
        InputStream inputStream = ResourceUtil.getResourceAsStream(GradleDependenciesParserTest.class, resource)
        DetectProject project = new DetectProject()
        GradleDependenciesParser gradleDependenciesParser = new GradleDependenciesParser()
        ReflectionTestUtils.setField(gradleDependenciesParser, 'externalIdFactory', externalIdFactory)
        DetectCodeLocation codeLocation = gradleDependenciesParser.parseDependencies(project, inputStream)
        return codeLocation;
    }

    @Test
    public void testSpringFrameworkAop() {
        InputStream inputStream = ResourceUtil.getResourceAsStream(GradleDependenciesParserTest.class, 'gradle/spring-framework/spring_aop_dependencyGraph.txt')
        DetectProject project = new DetectProject()
        GradleDependenciesParser gradleDependenciesParser = new GradleDependenciesParser()
        ReflectionTestUtils.setField(gradleDependenciesParser, 'externalIdFactory', new ExternalIdFactory())
        DetectCodeLocation codeLocation = gradleDependenciesParser.parseDependencies(project, inputStream)
        println(new GsonBuilder().setPrettyPrinting().create().toJson(codeLocation))
    }

    private void createNewCodeLocationTest(String gradleInspectorOutputResourcePath, String expectedResourcePath, String rootProjectName, String rootProjectVersionName) {
        DetectProject project = new DetectProject()
        GradleDependenciesParser gradleDependenciesParser = new GradleDependenciesParser()
        ReflectionTestUtils.setField(gradleDependenciesParser, 'externalIdFactory', new ExternalIdFactory())
        DetectCodeLocation codeLocation = gradleDependenciesParser.parseDependencies(project, ResourceUtil.getResourceAsStream(GradleDependenciesParserTest.class, gradleInspectorOutputResourcePath))

        assertEquals(rootProjectName, project.getProjectName())
        assertEquals(rootProjectVersionName, project.getProjectVersionName())
        testUtil.testJsonResource(expectedResourcePath, codeLocation)
    }
}
