/**
 * detectable
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
package com.synopsys.integration.detectable.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.synopsys.integration.detect.commontest.FileUtil;
import com.synopsys.integration.util.ResourceUtil;

public class FunctionalTestFiles {
    private static final String resourcePrefix = "/detectables/functional";

    public static String asString(final String relativeResourcePath) {
        final String data;
        try {
            data = ResourceUtil.getResourceAsString(FunctionalTestFiles.class, resourcePrefix + relativeResourcePath, StandardCharsets.UTF_8);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

        return Arrays.stream(data.split("\r?\n"))
                   .collect(Collectors.joining(System.lineSeparator()));
    }

    public static List<String> asListOfStrings(final String relativeResourcePath) {
        final String data = asString(relativeResourcePath);

        return Arrays.asList(data.split(System.lineSeparator()));
    }

    public static InputStream asInputStream(final String relativeResourcePath) {
        return FunctionalTestFiles.class.getResourceAsStream(resourcePrefix + relativeResourcePath);
    }

    public static String resolvePath(final String relativeResourcePath) {
        return FunctionalTestFiles.asFile(relativeResourcePath).getAbsolutePath();
    }

    public static File asFile(final String relativeResourcePath) {
        return FileUtil.asFile(FunctionalTestFiles.class, relativeResourcePath, resourcePrefix);
    }

}
