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
package com.synopsys.integration.detectable.detectables.gradle.unit;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.synopsys.integration.detectable.detectables.gradle.GradleReportLine;

public class GradleReportParserTest {
    //private final TestUtil testUtil = new TestUtil();

    @Test
    public void getLineLevelTest() {
        assertEquals(5, new GradleReportLine(("|    |         |    |    \\--- org.springframework:spring-core:4.3.5.RELEASE")).getTreeLevel());
        assertEquals(3, new GradleReportLine(("|    |         \\--- com.squareup.okhttp3:okhttp:3.4.2 (*)")).getTreeLevel());
        assertEquals(4, new GradleReportLine(("     |    |         \\--- org.ow2.asm:asm:5.0.3")).getTreeLevel());
        assertEquals(1, new GradleReportLine(("     +--- org.hamcrest:hamcrest-core:1.3")).getTreeLevel());
        assertEquals(0, new GradleReportLine(("+--- org.springframework.boot:spring-boot-starter: -> 1.4.3.RELEASE")).getTreeLevel());
        assertEquals(0, new GradleReportLine(("\\--- org.apache.commons:commons-compress:1.13")).getTreeLevel());
    }

}
