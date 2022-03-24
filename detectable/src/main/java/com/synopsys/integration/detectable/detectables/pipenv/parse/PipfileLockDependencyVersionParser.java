package com.synopsys.integration.detectable.detectables.pipenv.parse;

import org.jetbrains.annotations.Nullable;

public class PipfileLockDependencyVersionParser {
    public String parseRawVersion(@Nullable String rawVersion) {
        if (rawVersion == null) {
            return null;
        }
        return rawVersion.replace("==", "");
    }
}
