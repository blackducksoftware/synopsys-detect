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

import org.junit.Before
import org.junit.Test

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.detect.testutils.JsonTestUtil

public class CocoapodsPackagerTest {
    private final JsonTestUtil jsonTestUtil= new JsonTestUtil()
    private final CocoapodsPackager cocoapodsPackager = new CocoapodsPackager()

    @Before
    void init() {
        cocoapodsPackager.setPodLockParser(new PodLockParser())
    }

    @Test
    void simpleTest() {
        final String podlockText = jsonTestUtil.getResourceAsUTF8String('/cocoapods/simplePodfile.lock')
        final Set<DependencyNode> projectDependencies = cocoapodsPackager.extractProjectDependencies(podlockText) as Set
        jsonTestUtil.testJsonResource('/cocoapods/simpleExpected.json', projectDependencies)
    }

    @Test
    void complexTest() {
        final String podlockText = jsonTestUtil.getResourceAsUTF8String('/cocoapods/complexPodfile.lock')
        final Set<DependencyNode> projectDependencies = cocoapodsPackager.extractProjectDependencies(podlockText) as Set
        jsonTestUtil.testJsonResource('/cocoapods/complexExpected.json', projectDependencies)
    }
}
