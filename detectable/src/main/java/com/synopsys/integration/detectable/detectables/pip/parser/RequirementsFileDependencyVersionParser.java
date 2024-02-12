package com.synopsys.integration.detectable.detectables.pip.parser;

import org.jetbrains.annotations.Nullable;

public class RequirementsFileDependencyVersionParser {
    public String parseRawVersion(@Nullable String rawVersion) {
        if (rawVersion == null) {
            return null;
        }
        return rawVersion.replace("==", "");
    }
}
