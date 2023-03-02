package com.synopsys.integration.detect.configuration;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
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
        List<String> detectVersionFileContents = readDetectVersionFile();
        String versionText = parseValueFromVersionFileContents(detectVersionFileContents, "version");
        String buildDateText = parseValueFromVersionFileContents(detectVersionFileContents, "builddate");
        OperatingSystemType os = findOperatingSystemType();
        logger.debug(String.format("You seem to be running in a %s operating system.", os));
        logger.debug(String.format("You seem to be using %s architecture.", StringUtils.join(findArchitectures(), ", ")));
        return new DetectInfo(versionText, os, buildDateText);
    }

    public List<String> findArchitectures() {
        return Bds.of(System.getProperty("os.arch"), System.getenv("PROCESSOR_ARCHITECTURE"), System.getenv("PROCESSOR_ARCHITEW6432"))
            .filter(StringUtils::isNotBlank)
            .toList();
    }

    public List<String> readDetectVersionFile() {
        try {
            String versionFileContents = ResourceUtil.getResourceAsString(this.getClass(), "/version.txt", StandardCharsets.UTF_8.toString());
            return Arrays.asList(versionFileContents.split("\n"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String parseValueFromVersionFileContents(List<String> versionFileContents, String variableName) {
        return versionFileContents.stream()
            .filter(s -> s.startsWith(variableName + "="))
            .map(s -> StringUtils.removeStart(s, variableName + "="))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Error parsing version string from version file"));
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
