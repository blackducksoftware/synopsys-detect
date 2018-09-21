package com.blackducksoftware.integration.hub.detect.version;

import java.util.Objects;
import java.util.regex.Pattern;

public class DetectVersion {
    private final int majorVersion;
    private final int minorVersion;
    private final int patchVersion;

    public DetectVersion(final int majorVersion, final int minorVersion, final int patchVersion) {
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
        this.patchVersion = patchVersion;
    }

    public static DetectVersion fromString(final String rawVersion) {
        final String[] pieces = rawVersion.split(Pattern.quote("."));
        if (pieces.length != 3)
            throw new IllegalArgumentException("Version must have three pieces separated by a dot.");

        final int majorVersion = Integer.valueOf(pieces[0]);
        final int minorVersion = Integer.valueOf(pieces[1]);
        final int patchVersion = Integer.valueOf(pieces[2]);

        return new DetectVersion(majorVersion, minorVersion, patchVersion);
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

    public String toVersionString() {
        return String.format("%d.%d.%d", this.majorVersion, this.minorVersion, this.patchVersion);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        final DetectVersion that = (DetectVersion) o;
        return majorVersion == that.majorVersion &&
                   minorVersion == that.minorVersion &&
                   patchVersion == that.patchVersion;
    }

    @Override
    public int hashCode() {
        return Objects.hash(majorVersion, minorVersion, patchVersion);
    }
}