/*
 * common-test
 *
 * Copyright (c) 2021 Synopsys, Inc.
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
package com.synopsys.integration.detect.commontest;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.jupiter.api.Assertions;

public class FileUtil {
    public static File asFile(Class<?> resourceLoadingClass, final String relativeResourcePath, final String prefix) {
        final URL resource = resourceLoadingClass.getResource(prefix + relativeResourcePath);
        Assertions.assertNotNull(resource, "Could not find resource path: " + prefix + relativeResourcePath);
        final File file;
        try {
            file = new File(resource.toURI().getPath());
        } catch (URISyntaxException e) {
            throw new RuntimeException("Invalid file: " + resource.getPath());
        }
        Assertions.assertTrue(file.exists());

        return file;
    }

}
