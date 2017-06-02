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
package com.blackducksoftware.integration.hub.packman.packagemanager.cocoapods;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode;
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge;
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.NameVersionExternalId;
import com.blackducksoftware.integration.hub.packman.bomtool.cocoapods.CocoapodsPackager;
import com.blackducksoftware.integration.hub.packman.bomtool.cocoapods.PodLockParser;
import com.blackducksoftware.integration.hub.packman.util.ProjectInfoGatherer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class CocoapodsPackagerTest {
    public Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Test
    public void complexTest() throws JSONException, IOException, URISyntaxException {
        final Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        final String expected = IOUtils.toString(getClass().getResourceAsStream("/cocoapods/complex/Complex.json"), StandardCharsets.UTF_8);
        final File podlockFile = new File(getClass().getResource("/cocoapods/complex/Podfile.lock").toURI());
        final CocoapodsPackager cocoapodsPackager = new CocoapodsPackager();
        cocoapodsPackager.setProjectInfoGatherer(new ProjectInfoGatherer());
        cocoapodsPackager.setPodLockParser(new PodLockParser());
        final List<DependencyNode> projects = cocoapodsPackager.makeDependencyNodes(podlockFile.getParentFile().getAbsolutePath());
        assertEquals(1, projects.size());
        fixVersion(projects.get(0), "1.0.0");
        final String actual = gson.toJson(projects);
        System.out.println(actual);
        JSONAssert.assertEquals(expected, actual, false);
    }

    @Test
    public void simpleTest() throws JSONException, IOException, URISyntaxException {
        final Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        final String expected = IOUtils.toString(getClass().getResourceAsStream("/cocoapods/simple/Simple.json"), StandardCharsets.UTF_8);
        final File podlockFile = new File(getClass().getResource("/cocoapods/simple/Podfile.lock").toURI());
        final CocoapodsPackager cocoapodsPackager = new CocoapodsPackager();
        cocoapodsPackager.setProjectInfoGatherer(new ProjectInfoGatherer());
        cocoapodsPackager.setPodLockParser(new PodLockParser());
        final List<DependencyNode> projects = cocoapodsPackager.makeDependencyNodes(podlockFile.getParentFile().getAbsolutePath());
        assertEquals(1, projects.size());
        fixVersion(projects.get(0), "0.0.1");
        final String actual = gson.toJson(projects);
        JSONAssert.assertEquals(expected, actual, false);
    }

    private void fixVersion(final DependencyNode node, final String newVersion) {
        node.version = newVersion;
        node.externalId = new NameVersionExternalId(Forge.COCOAPODS, node.name, newVersion);
    }
}
