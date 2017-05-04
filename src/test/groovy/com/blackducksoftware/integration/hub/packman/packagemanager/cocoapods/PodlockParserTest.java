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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import com.blackducksoftware.integration.hub.packman.packagemanager.cocoapods.PodLock;
import com.blackducksoftware.integration.hub.packman.packagemanager.cocoapods.PodLockParser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class PodlockParserTest {
    @Test
    public void invalidPodlockParserTest() throws IOException {
        final String invalid = IOUtils.toString(this.getClass().getResourceAsStream("/cocoapods/Invalid.lock"), StandardCharsets.UTF_8);
        final PodLockParser parser = new PodLockParser();
        final PodLock podLock = parser.parse(invalid);
        assertNull(podLock);
    }

    @Test
    public void podlockParserSimpleTest() throws IOException, JSONException {
        final PodLockParser parser = new PodLockParser();
        final Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

        final String actualText = IOUtils.toString(this.getClass().getResourceAsStream("/cocoapods/simple/Podfile.lock"), StandardCharsets.UTF_8);
        final PodLock actualPodlock = parser.parse(actualText);
        assertNotNull(actualPodlock);

        final String expected = IOUtils.toString(this.getClass().getResourceAsStream("/cocoapods/simple/expected.json"),
                StandardCharsets.UTF_8);
        final String actual = gson.toJson(actualPodlock);
        JSONAssert.assertEquals(expected, actual, false);
    }

    @Test
    public void podlockParserComplexTest() throws IOException, JSONException {
        final PodLockParser parser = new PodLockParser();
        final Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

        final String actualText = IOUtils.toString(this.getClass().getResourceAsStream("/cocoapods/complex/Podfile.lock"), StandardCharsets.UTF_8);
        final PodLock actualPodlock = parser.parse(actualText);
        assertNotNull(actualPodlock);

        final String expected = IOUtils.toString(this.getClass().getResourceAsStream("/cocoapods/complex/expected.json"),
                StandardCharsets.UTF_8);
        final String actual = gson.toJson(actualPodlock);
        JSONAssert.assertEquals(expected, actual, false);
    }
}
