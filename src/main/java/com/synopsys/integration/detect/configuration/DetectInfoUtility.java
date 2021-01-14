/**
 * synopsys-detect
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
package com.synopsys.integration.detect.configuration;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.util.OperatingSystemType;
import com.synopsys.integration.util.ResourceUtil;

public class DetectInfoUtility {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static DetectInfo createDefaultDetectInfo() {
        return new DetectInfoUtility().createDetectInfo();
    }

    public DetectInfo createDetectInfo() {
        String versionText = findDetectVersionFromResources();
        int majorVersion = parseMajorVersion(versionText);
        OperatingSystemType os = findOperatingSystemType();
        logger.debug("You seem to be running in a " + os + " operating system.");
        return new DetectInfo(versionText, majorVersion, os);
    }

    public String findDetectVersionFromResources() {
        try {
            return ResourceUtil.getResourceAsString(this.getClass(), "/version.txt", StandardCharsets.UTF_8.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public OperatingSystemType findOperatingSystemType() {
        if (SystemUtils.IS_OS_LINUX) {
            return OperatingSystemType.LINUX;
        } else if (SystemUtils.IS_OS_MAC) {
            return OperatingSystemType.MAC;
        } else if (SystemUtils.IS_OS_WINDOWS) {
            return OperatingSystemType.WINDOWS;
        }

        logger.warn("Your operating system is not supported. Linux will be assumed.");
        return OperatingSystemType.LINUX;
    }

    public int parseMajorVersion(String detectVersionText) {
        return Integer.parseInt(detectVersionText.split(Pattern.quote("."))[0]);
    }
}
