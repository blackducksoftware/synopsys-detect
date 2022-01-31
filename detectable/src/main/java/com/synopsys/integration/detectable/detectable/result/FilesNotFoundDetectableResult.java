package com.synopsys.integration.detectable.detectable.result;

import java.util.Arrays;
import java.util.List;

public class FilesNotFoundDetectableResult extends FailedDetectableResult {
    private final List<String> patterns;

    public FilesNotFoundDetectableResult(String... patterns) {
        this.patterns = Arrays.asList(patterns);
    }

    public FilesNotFoundDetectableResult(List<String> patterns) {
        this.patterns = patterns;
    }

    @Override
    public String toDescription() {
        return "No files were found with any of the patterns: " + String.join(",", patterns);
    }
}
