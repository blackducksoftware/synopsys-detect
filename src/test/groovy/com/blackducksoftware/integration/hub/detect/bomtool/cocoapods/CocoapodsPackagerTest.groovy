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

import static org.junit.Assert.assertEquals;

import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode;
import com.blackducksoftware.integration.hub.detect.Application;
import com.blackducksoftware.integration.hub.detect.util.ProjectInfoGatherer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class)
@SpringBootTest
public class CocoapodsPackagerTest {
    public Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Autowired
    ProjectInfoGatherer projectInfoGatherer;

    @Test
    public void complexTest() throws JSONException, IOException, URISyntaxException {
        final Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        String expected = getClass().getResourceAsStream('/cocoapods/complex/Complex.json').getText(StandardCharsets.UTF_8.name())
        final File podlockFile = new File(getClass().getResource("/cocoapods/complex/Podfile.lock").toURI());
        final CocoapodsPackager cocoapodsPackager = new CocoapodsPackager();
        cocoapodsPackager.setPodLockParser(new PodLockParser());
        final List<DependencyNode> projectDependencies = cocoapodsPackager.extractProjectDependencies(podlockFile.text);
        assertEquals(66, projectDependencies.size());
        final String actual = gson.toJson(projectDependencies);
        JSONAssert.assertEquals(expected, actual, false);
    }

    @Test
    public void simpleTest() throws JSONException, IOException, URISyntaxException {
        final Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        final String expected = IOUtils.toString(getClass().getResourceAsStream("/cocoapods/simple/Simple.json"), StandardCharsets.UTF_8);
        final File podlockFile = new File(getClass().getResource("/cocoapods/simple/Podfile.lock").toURI());
        final CocoapodsPackager cocoapodsPackager = new CocoapodsPackager();
        cocoapodsPackager.setPodLockParser(new PodLockParser());
        final List<DependencyNode> projectDependencies = cocoapodsPackager.extractProjectDependencies(podlockFile.text);
        assertEquals(3, projectDependencies.size());
        final String actual = gson.toJson(projectDependencies);
        JSONAssert.assertEquals(expected, actual, false);
    }
}
