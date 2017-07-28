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
package com.blackducksoftware.integration.hub.detect.bomtool.cocoapods

import static org.junit.Assert.assertEquals

import java.nio.charset.StandardCharsets

import org.json.JSONException
import org.junit.Before
import org.junit.Test
import org.skyscreamer.jsonassert.JSONAssert

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.detect.testutils.JsonTestUtil
import com.google.gson.Gson
import com.google.gson.GsonBuilder

public class CocoapodsPackagerTest {
    private final JsonTestUtil jsonTestUtil= new JsonTestUtil()
    private final CocoapodsPackager cocoapodsPackager = new CocoapodsPackager()

    @Before
    void init() {
        cocoapodsPackager.setPodLockParser(new PodLockParser())
    }

    @Test
    void simpleTest() throws JSONException, IOException, URISyntaxException {
        final String podlockText = jsonTestUtil.getResourceAsUTF8String('/cocoapods/simple/Podfile.lock')
        final Set<DependencyNode> projectDependencies = cocoapodsPackager.extractProjectDependencies(podlockText) as Set
        assertEquals(3, projectDependencies.size())
        jsonTestUtil.testJsonResource('/cocoapods/simple/Simple.json', projectDependencies)
    }

    @Test
    void complexTest() throws JSONException, IOException, URISyntaxException {
        final Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
        String expected = getClass().getResourceAsStream('/cocoapods/simple/Simple.json').getText(StandardCharsets.UTF_8.name())
        final File podlockFile = new File(getClass().getResource("/cocoapods/simple/Podfile.lock").toURI())
        final CocoapodsPackager cocoapodsPackager = new CocoapodsPackager()
        cocoapodsPackager.setPodLockParser(new PodLockParser())
        final Set<DependencyNode> projectDependencies = cocoapodsPackager.extractProjectDependencies(podlockFile.text) as Set
        assertEquals(3, projectDependencies.size())
        final String actual = gson.toJson(projectDependencies)
        JSONAssert.assertEquals(expected, actual, false)
    }
}
