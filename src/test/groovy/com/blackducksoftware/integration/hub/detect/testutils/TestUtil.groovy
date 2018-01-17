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
package com.blackducksoftware.integration.hub.detect.testutils

import java.nio.charset.StandardCharsets

import org.skyscreamer.jsonassert.JSONAssert

import com.blackducksoftware.integration.util.ResourceUtil
import com.google.gson.Gson
import com.google.gson.GsonBuilder

class TestUtil {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create()

    void testJsonResource(String expectedResourcePath, Object object) {
        final String expected = getResourceAsUTF8String(expectedResourcePath)
        final String actual = gson.toJson(object)
        println actual
        testJson(expected, actual)
    }

    void testJson(String expectedJson, String actualJson) {
        JSONAssert.assertEquals(expectedJson, actualJson, false)
    }

    String getResourceAsUTF8String(String resourcePath) {
        String data = ResourceUtil.getResourceAsString(getClass(), resourcePath, StandardCharsets.UTF_8.toString())
        data.split("\r?\n").join(System.lineSeparator)
    }

    void createExpectedFile(String resourcePath, Object expectedObject) {
        final String expectedJson = gson.toJson(expectedObject)
        final File outputFile = new File('src/test/resources', resourcePath)
        outputFile.delete()
        outputFile << expectedJson
    }
}
