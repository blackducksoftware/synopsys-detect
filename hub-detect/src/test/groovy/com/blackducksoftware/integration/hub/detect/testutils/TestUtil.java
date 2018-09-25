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
package com.blackducksoftware.integration.hub.detect.testutils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.synopsys.integration.util.ResourceUtil;

public class TestUtil {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public void testJsonResource(final String expectedResourcePath, final Object object) {
        final String expected = getResourceAsUTF8String(expectedResourcePath);
        final String actual = gson.toJson(object);
        System.out.println(actual);
        testJson(expected, actual);
    }

    public void testJson(final String expectedJson, final String actualJson) {
        try {
            JSONAssert.assertEquals(expectedJson, actualJson, false);
        } catch (final JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public String getResourceAsUTF8String(final String resourcePath) {
        final String data;
        try {
            data = ResourceUtil.getResourceAsString(getClass(), resourcePath, StandardCharsets.UTF_8.toString());
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        return Arrays.asList(data.split("\r?\n")).stream().collect(Collectors.joining(System.lineSeparator()));
    }

    public InputStream getResourceAsInputStream(final String resourcePath) {
        return getClass().getResourceAsStream(resourcePath);
    }

    public void createExpectedFile(final String resourcePath, final Object expectedObject) {
        final String expectedJson = gson.toJson(expectedObject);
        final File outputFile = new File("src/test/resources", resourcePath);
        outputFile.delete();
        try {
            FileUtils.write(outputFile, expectedJson, StandardCharsets.UTF_8);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}
