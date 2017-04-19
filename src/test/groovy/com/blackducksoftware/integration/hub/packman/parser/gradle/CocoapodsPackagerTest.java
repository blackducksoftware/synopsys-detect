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
package com.blackducksoftware.integration.hub.packman.parser.gradle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONParser;

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode;
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge;
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.NameVersionExternalId;
import com.blackducksoftware.integration.hub.packman.parser.cocoapods.CocoapodsPackager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class CocoapodsPackagerTest {

    public Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Test
    public void testEverything() throws JSONException {
        try (
                final InputStream complexPodfile = this.getClass().getResourceAsStream("/cocoapods/complex/Podfile");
                final InputStream complexPodlock = this.getClass().getResourceAsStream("/cocoapods/complex/Podfile.lock");
                final InputStream complexJson = this.getClass().getResourceAsStream("/cocoapods/complex/Complex.json");
        ) {
            final CocoapodsPackager packager = new CocoapodsPackager(complexPodfile, complexPodlock, null);
            final List<DependencyNode> targets = packager.makeDependencyNodes();

            for (final DependencyNode target : targets) {
                assertNotNull(target);
                assertNotNull(target.version);
                target.version = "1.0.0";
                target.externalId = new NameVersionExternalId(Forge.cocoapods, target.name, target.version);
            }

            final String expectedJson = streamToString(complexJson);
            final String actualJson = gson.toJson(targets);

            verifyJsonArraysEqual(expectedJson, actualJson);

        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

    }

    public String streamToString(final InputStream stream) {
        try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            final StringBuilder json = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                json.append(line);
                json.append("\n");
            }
            return json.toString();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void verifyJsonArraysEqual(final String expectedJson, final String actualJson) throws JSONException {
        final JSONArray expected = (JSONArray) JSONParser.parseJSON(expectedJson);
        final JSONArray actual = (JSONArray) JSONParser.parseJSON(actualJson);
        assertEquals(expected.length(), actual.length());
        JSONAssert.assertEquals(expected, actual, false);
    }
}
