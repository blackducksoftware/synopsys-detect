package com.synopsys.integration.detectable.detectable.result;

public class NpmNodeModulesNotFoundDetectableResult extends FailedDetectableResult {
    private final String directoryPath;

    public NpmNodeModulesNotFoundDetectableResult(String directoryPath) {
        this.directoryPath = directoryPath;
    }

    @Override
    public String toDescription() {
        return String.format(
            "A package.json was located in %s, but the node_modules folder was NOT located. Please run 'npm install' in that location and try again.",
            directoryPath
        );
    }
}