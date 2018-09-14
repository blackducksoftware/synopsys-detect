package com.blackducksoftware.integration.hub.detect;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.type.OperatingSystemType;
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
        logger.info("You seem to be running in a " + os + " operating system.");
        return new DetectInfo(versionText, majorVersion, os);
    }

    public String findDetectVersionFromResources() {
        try {
            return ResourceUtil.getResourceAsString(this.getClass(), "/version.txt", StandardCharsets.UTF_8.toString());
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public OperatingSystemType findOperatingSystemType() {
        OperatingSystemType operatingSystemType;
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
