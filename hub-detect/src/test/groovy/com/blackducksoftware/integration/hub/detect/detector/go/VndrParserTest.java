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
package com.blackducksoftware.integration.hub.detect.detector.go;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.blackducksoftware.integration.hub.detect.testutils.DependencyGraphResourceTestUtil;
import com.blackducksoftware.integration.hub.detect.testutils.TestUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.synopsys.integration.hub.bdio.graph.DependencyGraph;
import com.synopsys.integration.hub.bdio.model.externalid.ExternalIdFactory;

public class VndrParserTest {
    Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    @Test
    public void vndrParserTest() throws IOException {
        final TestUtil testUtil = new TestUtil();
        final VndrParser vndrParser = new VndrParser(new ExternalIdFactory());

        final String text = testUtil.getResourceAsUTF8String("/go/vendor.conf");
        final List<String> vendorConfContents = Arrays.asList(text.split("\r?\n"));
        final DependencyGraph dependencyGraph = vndrParser.parseVendorConf(vendorConfContents);

        Assert.assertNotNull(dependencyGraph);
        DependencyGraphResourceTestUtil.assertGraph("/go/Go_VndrExpected_graph.json", dependencyGraph);
    }
}
