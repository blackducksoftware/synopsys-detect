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

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.gradle.parse.GradleReportLine
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.gradle.parse.GradleReportParser
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation
import com.blackducksoftware.integration.hub.detect.model.DetectProject
import com.blackducksoftware.integration.hub.detect.testutils.TestUtil
import com.google.gson.GsonBuilder
import org.junit.Test
import org.springframework.test.util.ReflectionTestUtils

import static com.blackducksoftware.integration.hub.detect.testutils.DependencyGraphAssertions.*
import static org.junit.Assert.assertEquals

class GradleReportParserTest {
    private TestUtil testUtil = new TestUtil()
    private ExternalIdFactory externalIdFactory = new ExternalIdFactory()

    @Test
    public void getLineLevelTest() {
        assertEquals(5, new GradleReportLine(('|    |         |    |    \\--- org.springframework:spring-core:4.3.5.RELEASE')).treeLevel)
        assertEquals(3, new GradleReportLine(('|    |         \\--- com.squareup.okhttp3:okhttp:3.4.2 (*)')).treeLevel)
        assertEquals(4, new GradleReportLine(('     |    |         \\--- org.ow2.asm:asm:5.0.3')).treeLevel)
        assertEquals(1, new GradleReportLine(('     +--- org.hamcrest:hamcrest-core:1.3')).treeLevel)
        assertEquals(0, new GradleReportLine(('+--- org.springframework.boot:spring-boot-starter: -> 1.4.3.RELEASE')).treeLevel)
        assertEquals(0, new GradleReportLine(('\\--- org.apache.commons:commons-compress:1.13')).treeLevel)
    }

    @Test
    public void extractCodeLocationTest() {
        createNewCodeLocationTest('/gradle/dependencyGraph.txt', '/gradle/dependencyGraph-expected.json', "hub-detect", "2.0.0-SNAPSHOT")
    }

    @Test
    public void complexTest() {
        DetectCodeLocation codeLocation = build('/gradle/parse-tests/complex_dependencyGraph.txt');
        DependencyGraph graph = codeLocation.dependencyGraph;

        assertHasMavenGav(graph, "non-project:with-nested:1.0.0");
        assertHasMavenGav(graph, "solo:component:4.12");
        assertHasMavenGav(graph, "some.group:child:2.2.2");
        assertHasMavenGav(graph, "terminal:child:6.2.3");

        assertDoesNotHave(graph, "child-project");
        assertDoesNotHave(graph, "nested-parent");
        assertDoesNotHave(graph, "spring-webflux");
        assertDoesNotHave(graph, "spring-beans");
        assertDoesNotHave(graph, "spring-core");
        assertDoesNotHave(graph, "spring-web");
        assertDoesNotHave(graph, "should-suppress");

        assertHasRootMavenGavs(graph, "solo:component:4.12", "non-project:with-nested:1.0.0", "some.group:parent:5.0.0", "terminal:child:6.2.3");

        assertParentHasChildMavenGav("some.group:parent:5.0.0", graph, "some.group:child:2.2.2");
    }

    private DetectCodeLocation build(String resource) {
        InputStream inputStream = getClass().getResourceAsStream(resource)
        DetectProject project = new DetectProject()
        GradleReportParser gradleReportParser = new GradleReportParser()
        ReflectionTestUtils.setField(gradleReportParser, 'externalIdFactory', externalIdFactory)
        DetectCodeLocation codeLocation = gradleReportParser.parseDependencies(project, inputStream)
        return codeLocation;
    }

    @Test
    public void testSpringFrameworkAop() {
        InputStream inputStream = getClass().getResourceAsStream('/gradle/spring-framework/spring_aop_dependencyGraph.txt')
        DetectProject project = new DetectProject()
        GradleReportParser gradleReportParser = new GradleReportParser()
        ReflectionTestUtils.setField(gradleReportParser, 'externalIdFactory', new ExternalIdFactory())
        DetectCodeLocation codeLocation = gradleReportParser.parseDependencies(project, inputStream)
        println(new GsonBuilder().setPrettyPrinting().create().toJson(codeLocation))
    }

    private void createNewCodeLocationTest(String gradleInspectorOutputResourcePath, String expectedResourcePath, String rootProjectName, String rootProjectVersionName) {
        DetectProject project = new DetectProject()
        GradleReportParser gradleReportParser = new GradleReportParser()
        ReflectionTestUtils.setField(gradleReportParser, 'externalIdFactory', new ExternalIdFactory())
        DetectCodeLocation codeLocation = gradleReportParser.parseDependencies(project, getClass().getResourceAsStream(gradleInspectorOutputResourcePath))

        assertEquals(rootProjectName, project.getProjectName())
        assertEquals(rootProjectVersionName, project.getProjectVersionName())
        testUtil.testJsonResource(expectedResourcePath, codeLocation)
    }
}
