package com.blackducksoftware.integration.hub.detect.util;

import java.util.List;
import java.util.Optional;

import com.github.zafarkhaja.semver.Version;

public class VersionUtil {
    public static Optional<Version> bestMatch(final List<Version> versions, final String versionRange) {
        Version bestVersion = null;
        for (final Version foundVersion : versions) {
            if ((bestVersion == null || foundVersion.greaterThan(bestVersion)) && foundVersion.satisfies(versionRange)) {
                bestVersion = foundVersion;
            }
        }

        return Optional.ofNullable(bestVersion);
    }
}
