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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.blackducksoftware.integration.hub.detect.Application;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class)
@SpringBootTest
public class ProjectInfoGathererTest {
    private final String testName = "Name";

    private final String testName2 = "Name2";

    private final String testVersion = "1.0.0";

    @Autowired
    ProjectInfoGatherer projectInfoGatherer;

    @Test
    public void extractFinalPieceFromPath() {
        assertEquals('a', projectInfoGatherer.extractFinalPieceFromSourcePath('/a'))
        assertEquals('a', projectInfoGatherer.extractFinalPieceFromSourcePath('/a/'))
        assertEquals('c', projectInfoGatherer.extractFinalPieceFromSourcePath('/a/b/c'))
        assertEquals('c', projectInfoGatherer.extractFinalPieceFromSourcePath('/a/b/c/'))
    }
}
