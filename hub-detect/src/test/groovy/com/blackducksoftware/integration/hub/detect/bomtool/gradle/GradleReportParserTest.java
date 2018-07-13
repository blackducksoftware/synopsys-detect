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
package com.blackducksoftware.integration.hub.detect.bomtool.gradle;

import static com.blackducksoftware.integration.hub.detect.testutils.DependencyGraphAssertions.assertDoesNotHave;
import static com.blackducksoftware.integration.hub.detect.testutils.DependencyGraphAssertions.assertHasMavenGav;
import static com.blackducksoftware.integration.hub.detect.testutils.DependencyGraphAssertions.assertHasRootMavenGavs;
import static com.blackducksoftware.integration.hub.detect.testutils.DependencyGraphAssertions.assertParentHasChildMavenGav;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.junit.Test;

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolType;
import com.blackducksoftware.integration.hub.detect.testutils.TestUtil;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocation;
import com.blackducksoftware.integration.util.NameVersion;
import com.google.gson.GsonBuilder;

public class GradleReportParserTest {
    private final TestUtil testUtil = new TestUtil();

    @Test
    public void getLineLevelTest() {
        assertEquals(5, new GradleReportLine(("|    |         |    |    \\--- org.springframework:spring-core:4.3.5.RELEASE")).getTreeLevel());
        assertEquals(3, new GradleReportLine(("|    |         \\--- com.squareup.okhttp3:okhttp:3.4.2 (*)")).getTreeLevel());
        assertEquals(4, new GradleReportLine(("     |    |         \\--- org.ow2.asm:asm:5.0.3")).getTreeLevel());
        assertEquals(1, new GradleReportLine(("     +--- org.hamcrest:hamcrest-core:1.3")).getTreeLevel());
        assertEquals(0, new GradleReportLine(("+--- org.springframework.boot:spring-boot-starter: -> 1.4.3.RELEASE")).getTreeLevel());
        assertEquals(0, new GradleReportLine(("\\--- org.apache.commons:commons-compress:1.13")).getTreeLevel());
    }

    @Test
    public void extractCodeLocationTest() throws IOException {
        createNewCodeLocationTest("src/test/resources/gradle/dependencyGraph.txt", "/gradle/dependencyGraph-expected.json", "hub-detect", "2.0.0-SNAPSHOT");
    }

    @Test
    public void complexTest() throws IOException {
        final DetectCodeLocation codeLocation = build("src/test/resources/gradle/parse-tests/complex_dependencyGraph.txt");
        final DependencyGraph graph = codeLocation.getDependencyGraph();

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

    private DetectCodeLocation build(final String resource) throws IOException {
        final File file = new File(resource);
        final GradleReportParser gradleReportParser = new GradleReportParser(new ExternalIdFactory());
        final Optional<DetectCodeLocation> result = gradleReportParser.parseDependencies(BomToolType.GRADLE_INSPECTOR, file);
        if (result.isPresent()) {
            return result.get();
        } else {
            return null;
        }
    }

    @Test
    public void testSpringFrameworkAop() throws IOException {
        final File file = new File("src/test/resources/gradle/spring-framework/spring_aop_dependencyGraph.txt");
        final GradleReportParser gradleReportParser = new GradleReportParser(new ExternalIdFactory());
        final Optional<DetectCodeLocation> result = gradleReportParser.parseDependencies(BomToolType.GRADLE_INSPECTOR, file);
        assertTrue(result.isPresent());
        System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(result.get()));
    }

    private void createNewCodeLocationTest(final String gradleInspectorOutputResourcePath, final String expectedResourcePath, final String rootProjectName, final String rootProjectVersionName) throws IOException {
        final GradleReportParser gradleReportParser = new GradleReportParser(new ExternalIdFactory());
        final Optional<DetectCodeLocation> result = gradleReportParser.parseDependencies(BomToolType.GRADLE_INSPECTOR, new File(gradleInspectorOutputResourcePath));
        final Optional<NameVersion> rootProjectNameVersion = gradleReportParser.parseRootProjectNameVersion(new File(gradleInspectorOutputResourcePath));

        assertTrue(result.isPresent());
        assertTrue(rootProjectNameVersion.isPresent());
        assertEquals(rootProjectName, rootProjectNameVersion.get().getName());
        assertEquals(rootProjectVersionName, rootProjectNameVersion.get().getVersion());
        testUtil.testJsonResource(expectedResourcePath, result.get());
    }
}
