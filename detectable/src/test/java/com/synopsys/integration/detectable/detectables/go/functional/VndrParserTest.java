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
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.go.vendr.parse.VndrParser;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;
import com.synopsys.integration.detectable.util.GraphCompare;

public class VndrParserTest {
    Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    @Test
    public void vndrParserTest() throws IOException {
        final VndrParser vndrParser = new VndrParser(new ExternalIdFactory());


        final List<String> vendorConfContents = Arrays.asList(FunctionalTestFiles.asString("/go/vendor.conf").split("\r?\n"));
        final DependencyGraph dependencyGraph = vndrParser.parseVendorConf(vendorConfContents);

        Assert.assertNotNull(dependencyGraph);
        GraphCompare.assertEqualsResource("/go/Go_VndrExpected_graph.json", dependencyGraph);
    }
}
