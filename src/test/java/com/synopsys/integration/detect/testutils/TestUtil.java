/**
 * synopsys-detect
 *
 * Copyright (c) 2020 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.synopsys.integration.detect.testutils;

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
        return Arrays.stream(data.split("\r?\n")).collect(Collectors.joining(System.lineSeparator()));
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
