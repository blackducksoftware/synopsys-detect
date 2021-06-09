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
package com.synopsys.integration.detect.battery.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.zeroturnaround.zip.commons.FileUtils;

import com.synopsys.integration.detect.commontest.FileUtil;
import com.synopsys.integration.util.ResourceUtil;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class BatteryFiles {
    public static final String DEFAULT_RESOURCE_PREFIX = "/battery";
    public static final String UTIL_RESOURCE_PREFIX = "/battery-util";

    public static String asString(final String relativeResourcePath) {
        return asString(relativeResourcePath, DEFAULT_RESOURCE_PREFIX);
    }

    public static String asString(final String relativeResourcePath, final String prefix) {
        final String path = prefix + relativeResourcePath;
        String data = null;
        try {
            data = ResourceUtil.getResourceAsString(BatteryFiles.class, path, StandardCharsets.UTF_8);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertNotNull(data, "Unable to find resource: " + path);

        return Arrays.stream(data.split("\r?\n"))
                   .collect(Collectors.joining(System.lineSeparator()));
    }

    public static List<String> asListOfStrings(final String relativeResourcePath) {
        return asListOfStrings(relativeResourcePath, DEFAULT_RESOURCE_PREFIX);
    }

    public static List<String> asListOfStrings(final String relativeResourcePath, final String prefix) {
        final String data = asString(relativeResourcePath, prefix);

        return Arrays.asList(data.split(System.lineSeparator()));
    }

    public static InputStream asInputStream(final String relativeResourcePath) {
        return asInputStream(relativeResourcePath, DEFAULT_RESOURCE_PREFIX);
    }

    public static InputStream asInputStream(final String relativeResourcePath, final String prefix) {
        return BatteryFiles.class.getResourceAsStream(prefix + relativeResourcePath);
    }

    public static Reader asReader(final String relativeResourcePath) {
        return asReader(relativeResourcePath, DEFAULT_RESOURCE_PREFIX);
    }

    public static Reader asReader(final String relativeResourcePath, final String prefix) {
        return new StringReader(asString(relativeResourcePath, prefix));
    }

    public static Template asTemplate(final String relativeResourcePath) throws IOException {
        return asTemplate(relativeResourcePath, DEFAULT_RESOURCE_PREFIX);
    }

    public static Template asTemplate(final String relativeResourcePath, final String prefix) throws IOException {
        return asTemplate(BatteryFiles.asFile(relativeResourcePath, prefix));
    }

    public static Template asTemplate(final File fullPath) throws IOException {
        final Configuration templates = new Configuration(Configuration.VERSION_2_3_26);
        return new Template(fullPath.getName(), FileUtils.readFileToString(fullPath), templates);
    }

    public static void processTemplate(final String relativeResourcePath, final File target, final Object model) throws IOException, TemplateException {
        processTemplate(relativeResourcePath, target, model, DEFAULT_RESOURCE_PREFIX);
    }

    public static void processTemplate(final String relativeResourcePath, final File target, final Object model, final String prefix) throws IOException, TemplateException {
        final Template resourceTemplate = BatteryFiles.asTemplate(relativeResourcePath, prefix);
        processTemplate(resourceTemplate, target, model);
    }

    public static void processTemplate(final File file, final File target, final Object model) throws IOException, TemplateException {
        processTemplate(asTemplate(file), target, model);
    }

    public static void processTemplate(final Template template, final File target, final Object model) throws IOException, TemplateException {
        try (final Writer fileWriter = new FileWriter(target)) {
            template.process(model, fileWriter);
        }
    }

    public static String resolvePath(final String relativeResourcePath) {
        return resolvePath(relativeResourcePath, DEFAULT_RESOURCE_PREFIX);
    }

    public static String resolvePath(final String relativeResourcePath, final String prefix) {
        return BatteryFiles.asFile(relativeResourcePath, prefix).getAbsolutePath();
    }

    public static File asFile(final String relativeResourcePath) {
        return asFile(relativeResourcePath, DEFAULT_RESOURCE_PREFIX);
    }

    public static File asFile(final String relativeResourcePath, final String prefix) {
        return FileUtil.asFile(DetectorBattery.class, relativeResourcePath, prefix);
    }
}
