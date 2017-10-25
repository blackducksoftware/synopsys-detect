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

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory
import com.blackducksoftware.integration.hub.detect.testutils.DependencyGraphResourceTestUtil
import com.google.gson.Gson
import com.google.gson.GsonBuilder

class GopkgLockParserTest {
    Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()

    @Test
    public void gopkgParserTest() throws IOException {
        final GopkgLockParser gopkgLockParser = new GopkgLockParser(new ExternalIdFactory())
        final String gopkgLockContents = IOUtils.toString(getClass().getResourceAsStream("/go/Gopkg.lock"), StandardCharsets.UTF_8)
        final DependencyGraph dependencyGraph = gopkgLockParser.parseDepLock(gopkgLockContents)
        Assert.assertNotNull(dependencyGraph)

        DependencyGraphResourceTestUtil.assertGraph('/go/Go_GopkgExpected_graph.json', dependencyGraph)
    }
}