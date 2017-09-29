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
package com.blackducksoftware.integration.hub.detect.bomtool.rubygems

import java.nio.charset.StandardCharsets

import org.apache.commons.io.IOUtils
import org.json.JSONException
import org.junit.Assert
import org.junit.Test
import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNodeTransformer
import com.blackducksoftware.integration.hub.detect.testutils.DependencyGraphTestUtil
import com.google.gson.Gson
import com.google.gson.GsonBuilder

class RubygemsNodePackagerTest {
    Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()

    @Test
    public void packagerTest() throws JSONException, IOException, URISyntaxException {
        final NameVersionNodeTransformer nameVersionNodeTransformer = new NameVersionNodeTransformer()
        final String expected = IOUtils.toString(getClass().getResourceAsStream("/rubygems/expectedPackager.json"),
                StandardCharsets.UTF_8)
        final String actualText = IOUtils.toString(getClass().getResourceAsStream("/rubygems/Gemfile.lock"),
                StandardCharsets.UTF_8)
        final RubygemsNodePackager rubygemsNodePackager = new RubygemsNodePackager()
        rubygemsNodePackager.nameVersionNodeTransformer = nameVersionNodeTransformer
        final DependencyGraph projects = rubygemsNodePackager.extractProjectDependencies(actualText)
        Assert.assertEquals(8, projects.getRootDependencies().size())

        DependencyGraphTestUtil.assertGraph('/rubygems/expectedPackager_graph.json', projects);
    }
}
