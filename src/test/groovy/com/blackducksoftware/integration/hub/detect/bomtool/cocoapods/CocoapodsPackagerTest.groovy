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
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.ExternalIdFactory
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNodeTransformer
import com.blackducksoftware.integration.hub.detect.testutils.TestUtil

public class CocoapodsPackagerTest {
    private final TestUtil testUtil= new TestUtil()
    private final CocoapodsPackager cocoapodsPackager = new CocoapodsPackager()

    @Before
    void init() {
        NameVersionNodeTransformer nameVersionNodeTransformer = new NameVersionNodeTransformer()
        nameVersionNodeTransformer.externalIdFactory = new ExternalIdFactory()
        cocoapodsPackager.nameVersionNodeTransformer = nameVersionNodeTransformer
    }

    @Test
    void simpleTest() {
        final String podlockText = testUtil.getResourceAsUTF8String('cocoapods/simplePodfile.lock')
        final Set<DependencyNode> projectDependencies = cocoapodsPackager.extractDependencyNodes(podlockText)
        testUtil.testJsonResource('cocoapods/simpleExpected.json', projectDependencies)
    }

    @Test
    void complexTest() {
        final String podlockText = testUtil.getResourceAsUTF8String('cocoapods/complexPodfile.lock')
        final Set<DependencyNode> projectDependencies = cocoapodsPackager.extractDependencyNodes(podlockText)
        testUtil.testJsonResource('cocoapods/complexExpected.json', projectDependencies)
    }
}
