package com.synopsys.integration.detectable.detectables.clang.dependencyfile;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DepsMkFileParser {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public List<String> parseDepsMk(final File depsMkFile) {
        try {
            final String depsMkText = FileUtils.readFileToString(depsMkFile, StandardCharsets.UTF_8);
            return parseDepsMk(depsMkText);
        } catch (final Exception e) {
            logger.warn(String.format("Error getting dependency file paths from '%s': %s", depsMkFile.getAbsolutePath(), e.getMessage()));
            return Collections.emptyList();
        }
    }

    public List<String> parseDepsMk(final String depsMkText) {
        final String[] depsMkTextParts = depsMkText.split(": ");
        if (depsMkTextParts.length != 2) {
            logger.warn(String.format("Unable to split mk text parts reasonably", depsMkTextParts));
            return Collections.emptyList();
        }
        String depsListString = depsMkTextParts[1];
        logger.trace(String.format("dependencies: %s", depsListString));

        depsListString = depsListString.replaceAll("\n", " ");
        logger.trace(String.format("dependencies, newlines removed: %s", depsListString));

        depsListString = depsListString.replaceAll("\\\\", " ");
        logger.trace(String.format("dependencies, backslashes removed: %s", depsListString));

        final String[] deps = depsListString.split("\\s+");
        for (final String includeFile : deps) {
            logger.trace(String.format("\t%s", includeFile));
        }
        return Arrays.asList(deps);
    }
}
