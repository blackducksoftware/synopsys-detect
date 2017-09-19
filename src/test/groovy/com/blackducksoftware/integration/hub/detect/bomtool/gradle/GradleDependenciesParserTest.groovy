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

import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation
import com.blackducksoftware.integration.hub.detect.model.DetectProject
import com.blackducksoftware.integration.hub.detect.testutils.TestUtil
import com.blackducksoftware.integration.util.ResourceUtil

class GradleDependenciesParserTest {
    private TestUtil testUtil = new TestUtil()

    @Test
    public void extractCodeLocationTest() {
        createNewCodeLocationTest('gradle/dependencyGraph.txt', '/gradle/dependencyGraph-expected.json', "", "")
    }

    private void createNewCodeLocationTest(String gradleInspectorOutputResourcePath, String expectedResourcePath, String rootProjectName, String rootProjectVersionName) {
        DetectProject project = new DetectProject()
        def gradleDependenciesParser = new GradleDependenciesParser()
        DetectCodeLocation codeLocation = gradleDependenciesParser.parseDependencies(project, ResourceUtil.getResourceAsStream(GradleDependenciesParserTest.class, gradleInspectorOutputResourcePath))
        assertEquals(rootProjectName, project.getProjectName())
        assertEquals(rootProjectVersionName, project.getProjectVersionName())
        testUtil.testJsonResource(expectedResourcePath, codeLocation)
    }
}
