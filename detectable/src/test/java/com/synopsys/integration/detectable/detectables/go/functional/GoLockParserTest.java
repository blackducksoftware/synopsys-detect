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
package com.synopsys.integration.detectable.detectables.go.functional;

import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.go.godep.parse.GoLockParser;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;
import com.synopsys.integration.detectable.util.GraphCompare;

public class GoLockParserTest {
    @Test
    public void gopkgParserTest() {
        final GoLockParser gopkgLockParser = new GoLockParser(new ExternalIdFactory());
        final InputStream gopkgLockInputStream = FunctionalTestFiles.asInputStream("/go/Gopkg.lock");
        final DependencyGraph dependencyGraph = gopkgLockParser.parseDepLock(gopkgLockInputStream);
        Assert.assertNotNull(dependencyGraph);

        GraphCompare.assertEqualsResource("/go/Go_GopkgExpected_graph.json", dependencyGraph);
    }
}
