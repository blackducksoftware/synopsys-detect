package com.blackducksoftware.integration.hub.detect.version;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.CompareToBuilder;

public class DetectVersionRange {

    private final int majorVersion;
    private final int minorVersion;
    private final int patchVersion;

    private final boolean isMajorWildcard;
    private final boolean isMinorWildcard;
    private final boolean isPatchWildcard;

    public DetectVersionRange(final int majorVersion, final int minorVersion, final int patchVersion, final boolean isMajorWildcard, final boolean isMinorWildcard, final boolean isPatchWildcard) {
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
        this.patchVersion = patchVersion;
        this.isMajorWildcard = isMajorWildcard;
        this.isMinorWildcard = isMinorWildcard;
        this.isPatchWildcard = isPatchWildcard;
    }

    public Optional<DetectVersion> bestMatch(final List<DetectVersion> versions) {
        final List<DetectVersion> possible = versions.stream()
                                                 .filter(it -> it.getPatchVersion() == patchVersion || isPatchWildcard)
                                                 .filter(it -> it.getMinorVersion() == minorVersion || isMinorWildcard)
                                                 .filter(it -> it.getMajorVersion() == majorVersion || isMajorWildcard)
                                                 .collect(Collectors.toList());

        final List<DetectVersion> sorted = possible.stream().sorted((o1, o2) -> new CompareToBuilder()
                                                                                    .append(o1.getMajorVersion(), o2.getMajorVersion())
                                                                                    .append(o1.getMinorVersion(), o2.getMinorVersion())
                                                                                    .append(o1.getPatchVersion(), o2.getPatchVersion())
                                                                                    .toComparison())
                                               .collect(Collectors.toList());

        Collections.reverse(sorted);

        return sorted.stream().findFirst();
    }

    public static DetectVersionRange fromString(final String rawVersion) {
        final String[] pieces = rawVersion.split(Pattern.quote("."));
        Optional<String> majorString = Optional.empty();
        Optional<String> minorString = Optional.empty();
        Optional<String> patchString = Optional.empty();
        if (pieces.length >= 1) {
            majorString = Optional.of(pieces[0]);
        }
        if (pieces.length >= 2) {
            minorString = Optional.of(pieces[1]);
        }
        if (pieces.length >= 3) {
            patchString = Optional.of(pieces[2]);
        }

        int major = 0;
        int minor = 0;
        int patch = 0;
        boolean majorWild = false;
        boolean minorWild = false;
        boolean patchWild = false;

        if (majorString.isPresent()) {
            if (majorString.get().equals("*") || majorString.get().equals("latest")) {
                majorWild = true;
                minorWild = true;
                patchWild = true;
            } else {
                major = Integer.valueOf(majorString.get());
            }
        }
        if (minorString.isPresent() && minorWild == false) {
            if (minorString.get().equals("*")) {
                minorWild = true;
                patchWild = true;
            } else {
                minor = Integer.valueOf(minorString.get());
            }
        }
        if (patchString.isPresent() && patchWild == false) {
            if (patchString.get().equals("*")) {
                patchWild = true;
            } else {
                patch = Integer.valueOf(patchString.get());
            }
        }
        return new DetectVersionRange(major, minor, patch, majorWild, minorWild, patchWild);
    }

    public int getMajorVersion() {
        return majorVersion;
    }

    public int getMinorVersion() {
        return minorVersion;
    }

    public int getPatchVersion() {
        return patchVersion;
    }

    public boolean isMajorWildcard() {
        return isMajorWildcard;
    }

    public boolean isMinorWildcard() {
        return isMinorWildcard;
    }

    public boolean isPatchWildcard() {
        return isPatchWildcard;
    }
}
