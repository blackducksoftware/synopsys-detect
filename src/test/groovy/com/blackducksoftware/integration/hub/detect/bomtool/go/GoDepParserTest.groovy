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
package com.blackducksoftware.integration.hub.detect.bomtool.go;

import java.nio.charset.StandardCharsets

import org.apache.commons.io.IOUtils
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test
import org.skyscreamer.jsonassert.JSONAssert

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.NameVersionExternalId
import com.blackducksoftware.integration.hub.detect.bomtool.GoDepBomTool
import com.blackducksoftware.integration.hub.detect.util.ProjectInfoGatherer
import com.google.gson.Gson
import com.google.gson.GsonBuilder

public class GoDepParserTest {
    Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()

    ProjectInfoGatherer projectInfoGatherer = new ProjectInfoGatherer()

    @Test
    @Ignore
    //ejk: I'm ignoring this test since it appears that it suffers under the Go feature of using the latest "release" from github
    public void goDepParserTest() throws IOException {
        final GoGodepsParser goDepParser = new GoGodepsParser(gson, projectInfoGatherer);
        final String goDepOutput = IOUtils.toString(getClass().getResourceAsStream("/go/Godeps.json"), StandardCharsets.UTF_8);
        final DependencyNode node = goDepParser.parseGoDep(goDepOutput);
        Assert.assertNotNull(node)

        fixVersion(node, '1.0.0')
        final String actual = gson.toJson(node);
        final String expected = IOUtils.toString(getClass().getResourceAsStream("/go/goParserExpected.json"), StandardCharsets.UTF_8);
        JSONAssert.assertEquals(expected, actual, false);
    }

    private void fixVersion(final DependencyNode node, final String newVersion) {
        node.version = newVersion
        node.externalId = new NameVersionExternalId(GoDepBomTool.GOLANG, node.name, newVersion)
    }
}
