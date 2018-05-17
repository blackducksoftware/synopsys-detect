package com.blackducksoftware.integration.hub.detect.strategy.result;

import java.util.Arrays;
import java.util.stream.Collectors;

public class FilesNotFoundStrategyResult extends FailedStrategyResult {
    private final String[] patterns;

    public FilesNotFoundStrategyResult(final String[] patterns) {
        this.patterns = patterns;
    }

    @Override
    public String toDescription() {
        return "No files were found with any of the patterns: " + Arrays.asList(patterns).stream().collect(Collectors.joining(","));
    }
}
