/**
 * detect-configuration
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
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
package com.blackducksoftware.integration.hub.detect;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.type.OperatingSystemType;
import com.blackducksoftware.integration.util.ResourceUtil;
import com.google.gson.Gson;

@Component
public class DetectInfo {
    private final Logger logger = LoggerFactory.getLogger(DetectInfo.class);

    @Autowired
    private Gson gson;

    private OperatingSystemType currentOs = null;
    private String detectVersion;

    public void init() {
        try {
            detectVersion = ResourceUtil.getResourceAsString(getClass(), "/version.txt", StandardCharsets.UTF_8.toString());
            populateCurrentOs();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getDetectVersion() {
        return detectVersion;
    }

    public OperatingSystemType getCurrentOs() {
        return currentOs;
    }

    private void populateCurrentOs() {
        if (SystemUtils.IS_OS_LINUX) {
            currentOs = OperatingSystemType.LINUX;
        } else if (SystemUtils.IS_OS_MAC) {
            currentOs = OperatingSystemType.MAC;
        } else if (SystemUtils.IS_OS_WINDOWS) {
            currentOs = OperatingSystemType.WINDOWS;
        }

        if (currentOs == null) {
            logger.warn("Your operating system is not supported. Linux will be assumed.");
            currentOs = OperatingSystemType.LINUX;
        } else {
            logger.info("You seem to be running in a " + currentOs + " operating system.");
        }
    }

}
