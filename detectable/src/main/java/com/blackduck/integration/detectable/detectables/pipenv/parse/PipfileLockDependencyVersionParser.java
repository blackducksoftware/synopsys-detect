package com.blackduck.integration.detectable.detectables.pipenv.parse;

import org.jetbrains.annotations.Nullable;

public class PipfileLockDependencyVersionParser {
    public String parseRawVersion(@Nullable String rawVersion) {
        if (rawVersion == null) {
            return null;
        }
        return rawVersion.replace("==", "");
    }
}
