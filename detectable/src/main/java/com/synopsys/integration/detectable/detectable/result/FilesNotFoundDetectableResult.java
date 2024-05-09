package com.synopsys.integration.detectable.detectable.result;

import java.util.List;

public class FilesNotFoundDetectableResult extends FailedDetectableResult {
    private static final String PREFIX = "No files were found with any of the patterns: ";

    public FilesNotFoundDetectableResult(String... patterns) {
        super(PREFIX, String.join(",", patterns));
    }

    public FilesNotFoundDetectableResult(List<String> patterns) {
        super(PREFIX, String.join(",", patterns));
    }
}
