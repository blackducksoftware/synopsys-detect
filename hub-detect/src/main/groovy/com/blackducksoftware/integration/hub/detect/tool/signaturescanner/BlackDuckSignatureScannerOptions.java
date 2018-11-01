package com.blackducksoftware.integration.hub.detect.tool.signaturescanner;

public class BlackDuckSignatureScannerOptions {
    private final String[] signatureScannerPaths;
    private final String[] exclusionPatterns;
    private final String[] exclusionNamePatterns;

    private final Integer scanMemory;
    private final Integer parrallelProcessors;
    private final Boolean cleanupOutput;
    private final Boolean dryRun;
    private final Boolean snippetMatching;
    private final String codeLocationPrefix;
    private final String codeLocationSuffix;
    private final String additionalArguments;
    private final Integer maxDepth;

    public BlackDuckSignatureScannerOptions(final String[] signatureScannerPaths, final String[] exclusionPatterns, final String[] exclusionNamePatterns, final Integer scanMemory, final Integer parrallelProcessors,
        final Boolean cleanupOutput, final Boolean dryRun, final Boolean snippetMatching, final String codeLocationPrefix, final String codeLocationSuffix, final String additionalArguments, final Integer maxDepth) {
        this.signatureScannerPaths = signatureScannerPaths;
        this.exclusionPatterns = exclusionPatterns;
        this.exclusionNamePatterns = exclusionNamePatterns;
        this.scanMemory = scanMemory;
        this.parrallelProcessors = parrallelProcessors;
        this.cleanupOutput = cleanupOutput;
        this.dryRun = dryRun;
        this.snippetMatching = snippetMatching;
        this.codeLocationPrefix = codeLocationPrefix;
        this.codeLocationSuffix = codeLocationSuffix;
        this.additionalArguments = additionalArguments;
        this.maxDepth = maxDepth;
    }

    public String[] getSignatureScannerPaths() {
        return signatureScannerPaths;
    }

    public String[] getExclusionPatterns() {
        return exclusionPatterns;
    }

    public String[] getExclusionNamePatterns() {
        return exclusionNamePatterns;
    }

    public Integer getScanMemory() {
        return scanMemory;
    }

    public Integer getParrallelProcessors() {
        return parrallelProcessors;
    }

    public Boolean getCleanupOutput() {
        return cleanupOutput;
    }

    public Boolean getDryRun() {
        return dryRun;
    }

    public Boolean getSnippetMatching() {
        return snippetMatching;
    }

    public String getCodeLocationPrefix() {
        return codeLocationPrefix;
    }

    public String getCodeLocationSuffix() {
        return codeLocationSuffix;
    }

    public String getAdditionalArguments() {
        return additionalArguments;
    }

    public Integer getMaxDepth() {
        return maxDepth;
    }
}
