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
package com.synopsys.integration.detectable.detectables.cocoapods.functional;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.annotations.FunctionalTest;
import com.synopsys.integration.detectable.detectables.cocoapods.PodlockParser;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;
import com.synopsys.integration.detectable.util.GraphAssert;

@FunctionalTest
public class CocoapodsPackagerTest {
    private final PodlockParser podlockParser = new PodlockParser(new ExternalIdFactory());

    @Test
    public void simpleTest() throws IOException {
        final String podlockText = FunctionalTestFiles.asString("/cocoapods/simplePodfile.lock");
        final DependencyGraph projectDependencies = podlockParser.extractDependencyGraph(podlockText);
        GraphAssert.assertGraph("/cocoapods/simpleExpected_graph.json", projectDependencies);
    }

    @Test
    public void complexTest() throws IOException {
        final String podlockText = FunctionalTestFiles.asString("/cocoapods/complexPodfile.lock");
        final DependencyGraph projectDependencies = podlockParser.extractDependencyGraph(podlockText);
        GraphAssert.assertGraph("/cocoapods/complexExpected_graph.json", projectDependencies);
    }
}
