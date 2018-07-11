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
package com.blackducksoftware.integration.hub.detect.bomtool.cocoapods;

import java.io.IOException;

import org.junit.Test;

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory;
import com.blackducksoftware.integration.hub.detect.testutils.DependencyGraphResourceTestUtil;
import com.blackducksoftware.integration.hub.detect.testutils.TestUtil;

public class CocoapodsPackagerTest {

    private final TestUtil testUtil = new TestUtil();
    private final PodlockParser podlockParser = new PodlockParser(new ExternalIdFactory());

    @Test
    public void simpleTest() throws IOException {
        final String podlockText = testUtil.getResourceAsUTF8String("/cocoapods/simplePodfile.lock");
        final DependencyGraph projectDependencies = podlockParser.extractDependencyGraph(podlockText);
        DependencyGraphResourceTestUtil.assertGraph("/cocoapods/simpleExpected_graph.json", projectDependencies);
    }

    @Test
    public void complexTest() throws IOException {
        final String podlockText = testUtil.getResourceAsUTF8String("/cocoapods/complexPodfile.lock");
        final DependencyGraph projectDependencies = podlockParser.extractDependencyGraph(podlockText);
        DependencyGraphResourceTestUtil.assertGraph("/cocoapods/complexExpected_graph.json", projectDependencies);
    }
}
