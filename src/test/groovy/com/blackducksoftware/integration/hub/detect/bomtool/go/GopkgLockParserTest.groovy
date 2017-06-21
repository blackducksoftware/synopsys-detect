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
package com.blackducksoftware.integration.hub.detect.bomtool.go

import java.nio.charset.StandardCharsets

import org.apache.commons.io.IOUtils
import org.junit.Assert
import org.junit.Test
import org.skyscreamer.jsonassert.JSONAssert

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.detect.util.ProjectInfoGatherer
import com.google.gson.Gson
import com.google.gson.GsonBuilder

class GopkgLockParserTest {
    Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()

    ProjectInfoGatherer projectInfoGatherer = new ProjectInfoGatherer()

    @Test
    public void gopkgParserTest() throws IOException {
        final GopkgLockParser gopkgLockParser = new GopkgLockParser(projectInfoGatherer);
        final String gopkgLockContents = IOUtils.toString(getClass().getResourceAsStream("/go/Gopkg.lock"), StandardCharsets.UTF_8)
        final List<DependencyNode> dependencies = gopkgLockParser.parseDepLock(gopkgLockContents);
        Assert.assertNotNull(dependencies)

        final String actual = gson.toJson(dependencies)
        final String expected = IOUtils.toString(getClass().getResourceAsStream("/go/Go_GopkgExpected.json"), StandardCharsets.UTF_8)
        JSONAssert.assertEquals(expected, actual, false);
    }
}
