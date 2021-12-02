/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.configuration;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.common.util.Bds;
import com.synopsys.integration.util.OperatingSystemType;
import com.synopsys.integration.util.ResourceUtil;

public class DetectInfoUtility {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static DetectInfo createDefaultDetectInfo() {
        return new DetectInfoUtility().createDetectInfo();
    }

    public DetectInfo createDetectInfo() {
        String versionText = findDetectVersionFromResources();
        OperatingSystemType os = findOperatingSystemType();
        logger.debug(String.format("You seem to be running in a %s operating system.", os));
        logger.debug(String.format("You seem to be using %s architecture.", StringUtils.join(findArchitectures(), ", ")));
        return new DetectInfo(versionText, os);
    }

    public List<String> findArchitectures() {
        return Bds.of(System.getProperty("os.arch"), System.getenv("PROCESSOR_ARCHITECTURE"), System.getenv("PROCESSOR_ARCHITEW6432"))
            .filter(StringUtils::isNotBlank)
            .toList();
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
}
