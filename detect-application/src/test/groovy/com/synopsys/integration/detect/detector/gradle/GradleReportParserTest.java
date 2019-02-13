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
package com.synopsys.integration.detect.detector.gradle;

import static com.synopsys.integration.detect.testutils.DependencyGraphAssertions.assertDoesNotHave;
import static com.synopsys.integration.detect.testutils.DependencyGraphAssertions.assertHasMavenGav;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.junit.Test;

import com.synopsys.integration.detect.testutils.TestUtil;
import com.synopsys.integration.detect.workflow.codelocation.DetectCodeLocation;
import com.google.gson.GsonBuilder;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detect.testutils.DependencyGraphAssertions;
import com.synopsys.integration.util.NameVersion;

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
        createNewCodeLocationTest("src/test/resources/gradle/dependencyGraph.txt", "/gradle/dependencyGraph-expected.json", "src/test/resources/gradle/rootProjectMetadata.txt", "hub-detect", "2.0.0-SNAPSHOT");
    }

    @Test
    public void complexTest() throws IOException {
        final DetectCodeLocation codeLocation = build("src/test/resources/gradle/parse-tests/complex_dependencyGraph.txt");
        final DependencyGraph graph = codeLocation.getDependencyGraph();

        DependencyGraphAssertions.assertHasMavenGav(graph, "non-project:with-nested:1.0.0");
        DependencyGraphAssertions.assertHasMavenGav(graph, "solo:component:4.12");
        DependencyGraphAssertions.assertHasMavenGav(graph, "some.group:child:2.2.2");
        DependencyGraphAssertions.assertHasMavenGav(graph, "terminal:child:6.2.3");

        DependencyGraphAssertions.assertDoesNotHave(graph, "child-project");
        DependencyGraphAssertions.assertDoesNotHave(graph, "nested-parent");
        DependencyGraphAssertions.assertDoesNotHave(graph, "spring-webflux");
        DependencyGraphAssertions.assertDoesNotHave(graph, "spring-beans");
        DependencyGraphAssertions.assertDoesNotHave(graph, "spring-core");
        DependencyGraphAssertions.assertDoesNotHave(graph, "spring-web");
        DependencyGraphAssertions.assertDoesNotHave(graph, "should-suppress");

        DependencyGraphAssertions.assertHasRootMavenGavs(graph, "solo:component:4.12", "non-project:with-nested:1.0.0", "some.group:parent:5.0.0", "terminal:child:6.2.3");

        DependencyGraphAssertions.assertParentHasChildMavenGav("some.group:parent:5.0.0", graph, "some.group:child:2.2.2");
    }

    private DetectCodeLocation build(final String resource) throws IOException {
        final File file = new File(resource);
        final GradleReportParser gradleReportParser = new GradleReportParser(new ExternalIdFactory());
        final Optional<DetectCodeLocation> result = gradleReportParser.parseDependencies(file);
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
        final Optional<DetectCodeLocation> result = gradleReportParser.parseDependencies(file);
        assertTrue(result.isPresent());
        System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(result.get()));
    }

    @Test
    public void testImplementationsGraph() throws IOException {
        final File file = new File("src/test/resources/gradle/gradle_implementations_dependencyGraph.txt");
        final GradleReportParser gradleReportParser = new GradleReportParser(new ExternalIdFactory());
        final Optional<DetectCodeLocation> result = gradleReportParser.parseDependencies(file);
        assertTrue(result.isPresent());
        System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(result.get()));
    }

    private void createNewCodeLocationTest(final String gradleInspectorOutputFilePath, final String expectedResourcePath, final String rootProjectFilePath, final String rootProjectName, final String rootProjectVersionName)
        throws IOException {
        final GradleReportParser gradleReportParser = new GradleReportParser(new ExternalIdFactory());
        final Optional<DetectCodeLocation> result = gradleReportParser.parseDependencies(new File(gradleInspectorOutputFilePath));
        final Optional<NameVersion> rootProjectNameVersion = gradleReportParser.parseRootProjectNameVersion(new File(rootProjectFilePath));

        assertTrue(result.isPresent());
        assertTrue(rootProjectNameVersion.isPresent());
        assertEquals(rootProjectName, rootProjectNameVersion.get().getName());
        assertEquals(rootProjectVersionName, rootProjectNameVersion.get().getVersion());
        testUtil.testJsonResource(expectedResourcePath, result.get());
    }
}
