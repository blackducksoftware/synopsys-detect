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

import com.synopsys.integration.detectable.detectables.gradle.parse.GradleReportLine;

public class GradleReportParserTest {
    //private final TestUtil testUtil = new TestUtil();

    /*

------------------------------------------------------------
Project :complex-test - Tests different project positions, components and opening characters
------------------------------------------------------------

archives - test that we can handle no dependencies
No dependencies

projectTest - test that this project dependency does not show up
\--- project :child-project

junitTest - test an only component under a config that opens with indicator
\--- solo:component:4.12

dependency - Compile classpath for source set 'main'.
+--- project :nested-parent
|    \--- project :nested-child
\--- non-project:with-nested:1.0.0

compile
+--- project :spring-webflux
|    +--- project :spring-beans (*)
|    +--- project :spring-core (*)
|    +--- project :spring-web (*)
|    \--- should-suppress:project-child: -> 6
+--- some.group:parent:5.0.0
|    \--- some.group:child:2.2.2
\--- terminal:child:6.2.3ok

     */
    @Test
    public void parseNoDependencies() {

    }

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
