package com.synopsys.integration.detect.tool.binaryscanner;

import java.util.List;

public class BinaryScanOptions {
    private final String singleTargetFilePath;
    private final List<String> multipleTargetFileNamePatterns;

    private final String codeLocationPrefix;
    private final String codeLocationSuffix;

    public BinaryScanOptions(final String singleTargetFilePath, final List<String> multipleTargetFileNamePatterns, final String codeLocationPrefix, final String codeLocationSuffix) {
        this.singleTargetFilePath = singleTargetFilePath;
        this.multipleTargetFileNamePatterns = multipleTargetFileNamePatterns;
        this.codeLocationPrefix = codeLocationPrefix;
        this.codeLocationSuffix = codeLocationSuffix;
    }

    public List<String> getMultipleTargetFileNamePatterns() {
        return multipleTargetFileNamePatterns;
    }

    public String getSingleTargetFilePath() {
        return singleTargetFilePath;
    }

    public String getCodeLocationPrefix() {
        return codeLocationPrefix;
    }

    public String getCodeLocationSuffix() {
        return codeLocationSuffix;
    }
}
