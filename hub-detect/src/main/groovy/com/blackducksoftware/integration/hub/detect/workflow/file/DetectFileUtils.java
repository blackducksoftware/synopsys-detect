/**
 * hub-detect
 *
 * Copyright (C) 2019 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
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
package com.blackducksoftware.integration.hub.detect.workflow.file;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DetectFileUtils {
    private final static Logger logger = LoggerFactory.getLogger(DetectFileUtils.class);

    public static File writeToFile(final File file, final String contents) throws IOException {
        return writeToFile(file, contents, true);
    }

    private static File writeToFile(final File file, final String contents, final boolean overwrite) throws IOException {
        if (file == null) {
            return null;
        }
        if (overwrite && file.exists()) {
            file.delete();
        }
        if (file.exists()) {
            logger.info(String.format("%s exists and not being overwritten", file.getAbsolutePath()));
        } else {
            FileUtils.write(file, contents, StandardCharsets.UTF_8);
        }
        return file;
    }
}
