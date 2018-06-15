package com.blackducksoftware.integration.hub.detect.strategy.result;

public class NpmRunInstallStrategyResult extends FailedStrategyResult {
    private final String directoryPath;

    public NpmRunInstallStrategyResult(final String directoryPath) {
        this.directoryPath = directoryPath;
    }

    @Override
    public String toDescription() {
        return String.format("A package.json was located in %s, but the node_modules folder was NOT located. Please run 'npm install' in that location and try again.", directoryPath);
    }
}