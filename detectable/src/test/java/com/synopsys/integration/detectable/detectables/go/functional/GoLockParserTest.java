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

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.go.godep.parse.GoLockParser;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;
import com.synopsys.integration.detectable.util.GraphCompare;

public class GoLockParserTest {
    Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    @Test
    public void gopkgParserTest() throws IOException {
        final GoLockParser gopkgLockParser = new GoLockParser(new ExternalIdFactory());
        final String gopkgLockContents = FunctionalTestFiles.asString("/go/Gopkg.lock");
        final DependencyGraph dependencyGraph = gopkgLockParser.parseDepLock(gopkgLockContents);
        Assert.assertNotNull(dependencyGraph);

        GraphCompare.assertEqualsResource("/go/Go_GopkgExpected_graph.json", dependencyGraph);
    }
}
