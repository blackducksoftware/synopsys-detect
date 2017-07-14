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
import org.junit.Test
import org.skyscreamer.jsonassert.JSONAssert

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.NameVersionExternalId
import com.blackducksoftware.integration.hub.detect.bomtool.GoDepBomTool
import com.blackducksoftware.integration.hub.detect.bomtool.go.godep.GoGodepsParser
import com.google.gson.Gson
import com.google.gson.GsonBuilder

public class GoGodepsParserTest {
    Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()

    @Test
    public void goDepParserTest() throws IOException {
        final GoGodepsParser goDepParser = new GoGodepsParser(gson)
        final String goDepOutput = IOUtils.toString(getClass().getResourceAsStream("/go/Go_Godeps.json"), StandardCharsets.UTF_8)
        final List<DependencyNode> projectDependencies = goDepParser.extractProjectDependencies(goDepOutput)
        final String actual = gson.toJson(projectDependencies)
        final String expected = IOUtils.toString(getClass().getResourceAsStream("/go/Go_GodepsParserExpected.json"), StandardCharsets.UTF_8)
        JSONAssert.assertEquals(expected, actual, false)
    }

    private void fixVersion(final DependencyNode node, final String newVersion) {
        node.version = newVersion
        node.externalId = new NameVersionExternalId(GoDepBomTool.GOLANG, node.name, newVersion)
    }
}
