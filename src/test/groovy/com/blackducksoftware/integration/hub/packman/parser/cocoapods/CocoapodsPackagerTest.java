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
package com.blackducksoftware.integration.hub.packman.parser.cocoapods;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONParser;

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode;
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge;
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.NameVersionExternalId;
import com.blackducksoftware.integration.hub.packman.packagemanager.cocoapods.CocoapodsPackager;
import com.blackducksoftware.integration.hub.packman.util.InputStreamConverter;
import com.blackducksoftware.integration.hub.packman.util.OutputCleaner;
import com.blackducksoftware.integration.hub.packman.util.ProjectInfoGatherer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class CocoapodsPackagerTest {
    public Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Test
    public void nullStreamTest() {
        final CocoapodsPackager packager = new CocoapodsPackager(null, null, null, null, null, null);
        boolean failed = false;
        try {
            packager.makeDependencyNodes();
        } catch (final IOException | NullPointerException e) {
            failed = true;
        }
        assertEquals(true, failed);
    }

    @Test
    public void invalidFilePathTest() throws JSONException, IOException {
        final String podlockPath = "/invalid/path.lock";
        final String expectedJsonPath = "/inavlid/path.json";
        boolean failed = false;
        try {
            testPackagerWithFiles(podlockPath, null, expectedJsonPath, true);
        } catch (final NullPointerException e) {
            failed = true;
        }
        assertEquals(true, failed);
    }

    @Test
    public void complexTest() throws JSONException, IOException {
        final String podlockPath = "/cocoapods/complex/Podfile.lock";
        final String expectedJsonPath = "/cocoapods/complex/Complex.json";
        testPackagerWithFiles(podlockPath, null, expectedJsonPath, true);
    }

    @Test
    public void simpleTest() throws JSONException, IOException {
        final String podlockPath = "/cocoapods/simple/Podfile.lock";
        final String podspecPath = "/cocoapods/simple/BlackDuckSwiftSample.podspec";
        final String expectedJsonPath = "/cocoapods/simple/Simple.json";
        testPackagerWithFiles(podlockPath, podspecPath, expectedJsonPath, false);
    }

    private void testPackagerWithFiles(final String podlockPath, final String podspecPath, final String expectedJsonPath,
            final boolean fixVersion)
            throws JSONException, IOException {

        final InputStreamConverter inputStreamConverter = new InputStreamConverter();
        final OutputCleaner outputCleaner = new OutputCleaner();

        InputStream podlockStream = null;
        InputStream expectedJsonStream = null;
        InputStream podspecStream = null;
        try {
            podlockStream = this.getClass().getResourceAsStream(podlockPath);
            if (StringUtils.isNotBlank(podspecPath)) {
                podspecStream = this.getClass().getResourceAsStream(podspecPath);
            }
            expectedJsonStream = this.getClass().getResourceAsStream(expectedJsonPath);

            final ProjectInfoGatherer projectInfoGatherer = new ProjectInfoGatherer();
            final CocoapodsPackager packager = new CocoapodsPackager(projectInfoGatherer, inputStreamConverter, outputCleaner, podlockStream, podspecStream,
                    "/cocoapods/complex");
            final List<DependencyNode> targets = packager.makeDependencyNodes();

            if (fixVersion) {
                for (final DependencyNode target : targets) {
                    assertNotNull(target);
                    assertNotNull(target.version);
                    target.version = "1.0.0";
                    target.externalId = new NameVersionExternalId(Forge.cocoapods, target.name, target.version);
                }
            }

            final String expectedJson = IOUtils.toString(expectedJsonStream, StandardCharsets.UTF_8);
            final String actualJson = gson.toJson(targets);

            verifyJsonArraysEqual(expectedJson, actualJson);
        } finally {
            tryClose(podlockStream);
            tryClose(podspecStream);
            tryClose(expectedJsonStream);
        }
    }

    private void tryClose(final Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void verifyJsonArraysEqual(final String expectedJson, final String actualJson) throws JSONException {
        final JSONArray expected = (JSONArray) JSONParser.parseJSON(expectedJson);
        final JSONArray actual = (JSONArray) JSONParser.parseJSON(actualJson);
        assertEquals(expected.length(), actual.length());
        JSONAssert.assertEquals(expected, actual, false);
    }
}
