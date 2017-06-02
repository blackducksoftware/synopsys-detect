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
package com.blackducksoftware.integration.hub.packman.packagemanager.rubygems

import java.nio.charset.StandardCharsets

import org.apache.commons.io.IOUtils
import org.json.JSONException
import org.junit.Assert
import org.junit.Test
import org.skyscreamer.jsonassert.JSONAssert

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.NameVersionExternalId
import com.blackducksoftware.integration.hub.packman.util.ProjectInfoGatherer
import com.google.gson.Gson
import com.google.gson.GsonBuilder

class RubygemsNodePackagerTest {
    Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()

    @Test
    public void packagerTest() throws JSONException, IOException, URISyntaxException {
        final ProjectInfoGatherer projectInfoGatherer = new ProjectInfoGatherer()
        final String sourcePath = "/rubygems/"
        final String expected = IOUtils.toString(getClass().getResourceAsStream("/rubygems/expectedPackager.json"),
                StandardCharsets.UTF_8)
        final String actualText = IOUtils.toString(getClass().getResourceAsStream("/rubygems/Gemfile.lock"),
                StandardCharsets.UTF_8)
        final RubygemsNodePackager rubygemsNodePackager = new RubygemsNodePackager(projectInfoGatherer)
        final List<DependencyNode> projects = rubygemsNodePackager.makeDependencyNodes(sourcePath, actualText)
        Assert.assertEquals(1, projects.size())

        fixVersion(projects.get(0), "1.0.0")

        final String actual = gson.toJson(projects)
        System.out.println(actual)
        JSONAssert.assertEquals(expected, actual, false)
    }

    private void fixVersion(final DependencyNode node, final String newVersion) {
        node.version = newVersion
        node.externalId = new NameVersionExternalId(Forge.RUBYGEMS, node.name, newVersion)
    }
}
