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
package com.blackducksoftware.integration.hub.packman.util;

import static org.junit.Assert.assertEquals;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import com.blackducksoftware.integration.hub.packman.PackageManagerType;

public class ProjectInfoGathererTest {

    private ProjectInfoGatherer projectInfoGathererDefault;

    private ProjectInfoGatherer projectInfoGathererAggregate;

    private final String testName = "Name";

    private final String testName2 = "Name2";

    private final String testVersion = "1.0.0";

    private final String packageMangerName = PackageManagerType.MAVEN.toString().toLowerCase();

    @Before
    public void init() {
        projectInfoGathererDefault = new ProjectInfoGatherer();
        projectInfoGathererAggregate = new ProjectInfoGatherer();
        projectInfoGathererAggregate.projectName = testName2;
        projectInfoGathererAggregate.projectVersion = testVersion;
    }

    @Test
    public void getProjectNameFromPath() {
        final String projectName = projectInfoGathererDefault.getProjectName(PackageManagerType.MAVEN, "I/Am/A/Test/" + testName);
        final String projectVersion = projectInfoGathererDefault.getProjectVersion();
        assertEquals(testName + "_maven", projectName);
        assertEquals(DateTime.now().toString(ProjectInfoGatherer.DATE_FORMAT), projectVersion);
    }

    @Test
    public void getProjectNameFromDefault() {
        final String projectName = projectInfoGathererDefault.getProjectName(PackageManagerType.MAVEN, "I/Am/A/Test/" + testName, testName2);
        final String projectVersion = projectInfoGathererDefault.getProjectVersion(testVersion);
        assertEquals(testName2, projectName);
        assertEquals(testVersion, projectVersion);
    }

    @Test
    public void getProjectNameFromPathAggregate() {
        final String projectName = projectInfoGathererAggregate.getProjectName(PackageManagerType.MAVEN, "I/Am/A/Test/" + testName);
        final String projectVersion = projectInfoGathererAggregate.getProjectVersion();
        assertEquals(testName2, projectName);
        assertEquals(testVersion, projectVersion);
    }

    @Test
    public void getRawProjectNameAggregate() {
        assertEquals(testName2, projectInfoGathererAggregate.getRawProjectName());
    }
}
