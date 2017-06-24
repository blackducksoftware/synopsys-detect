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
package com.blackducksoftware.integration.hub.detect.util;

import static org.junit.Assert.assertEquals;

import org.joda.time.DateTime;
import org.junit.Test;

import com.blackducksoftware.integration.hub.detect.type.BomToolType;

public class ProjectInfoGathererTest {
    private final String testName = "Name";

    private final String testName2 = "Name2";

    private final String testVersion = "1.0.0";

    @Test
    public void getProjectNameFromPath() {
        final ProjectInfoGatherer projectInfoGatherer = new ProjectInfoGatherer();
        final String projectName = projectInfoGatherer.getProjectName(BomToolType.MAVEN, "I/Am/A/Test/" + testName);
        final String projectVersion = projectInfoGatherer.getProjectVersionName();
        assertEquals(testName + "_maven", projectName);
        final String aFewSecondsAfterTheValueWasCreated = DateTime.now().toString(ProjectInfoGatherer.DATE_FORMAT);

        assertEquals(aFewSecondsAfterTheValueWasCreated.substring(0, 17), projectVersion.substring(0, 17));
    }

    @Test
    public void getProjectNameFromDefault() {
        final ProjectInfoGatherer projectInfoGatherer = new ProjectInfoGatherer();
        final String projectName = projectInfoGatherer.getProjectName(BomToolType.MAVEN, "I/Am/A/Test/" + testName, testName2);
        final String projectVersion = projectInfoGatherer.getProjectVersionName(testVersion);
        assertEquals(testName2, projectName);
        assertEquals(testVersion, projectVersion);
    }

}
