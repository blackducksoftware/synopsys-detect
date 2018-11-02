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
package com.blackducksoftware.integration.hub.detect.detector.rubygems;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;

import com.blackducksoftware.integration.hub.detect.testutils.DependencyGraphResourceTestUtil;
import com.blackducksoftware.integration.hub.detect.testutils.TestUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.synopsys.integration.hub.bdio.graph.DependencyGraph;
import com.synopsys.integration.hub.bdio.model.externalid.ExternalIdFactory;

public class RubygemsNodePackagerTest {
    Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    TestUtil testUtils = new TestUtil();

    @Test
    public void packagerTest() throws JSONException, IOException, URISyntaxException {
        final List<String> actualText = Arrays.asList(testUtils.getResourceAsUTF8String("/rubygems/Gemfile.lock").split("\n"));
        final GemlockParser rubygemsNodePackager = new GemlockParser(new ExternalIdFactory());
        final DependencyGraph projects = rubygemsNodePackager.parseProjectDependencies(actualText);
        Assert.assertEquals(8, projects.getRootDependencies().size());

        DependencyGraphResourceTestUtil.assertGraph("/rubygems/expectedPackager_graph.json", projects);
    }
}
