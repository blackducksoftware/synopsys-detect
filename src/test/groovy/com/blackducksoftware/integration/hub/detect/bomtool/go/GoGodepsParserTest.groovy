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

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph
import com.blackducksoftware.integration.hub.bdio.model.dependency.Dependency
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory
import com.blackducksoftware.integration.hub.detect.bomtool.go.godep.GoGodepsParser
import com.blackducksoftware.integration.hub.detect.testutils.DependencyGraphResourceTestUtil
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.apache.commons.io.IOUtils
import org.junit.Test

import java.nio.charset.StandardCharsets

public class GoGodepsParserTest {
    Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
    ExternalIdFactory externalIdFactory = new ExternalIdFactory()

    @Test
    public void goDepParserTest() throws IOException {
        final GoGodepsParser goDepParser = new GoGodepsParser(gson, new ExternalIdFactory())
        final String goDepOutput = IOUtils.toString(getClass().getResourceAsStream("/go/Go_Godeps.json"), StandardCharsets.UTF_8)
        final DependencyGraph dependencyGraph = goDepParser.extractProjectDependencies(goDepOutput)

        DependencyGraphResourceTestUtil.assertGraph('/go/Go_GodepsParserExpected_graph.json', dependencyGraph)
    }

    private void fixVersion(final Dependency node, final String newVersion) {
        node.version = newVersion
        node.externalId = externalIdFactory.createNameVersionExternalId(GoDepBomTool.GOLANG, node.name, newVersion)
    }
}
