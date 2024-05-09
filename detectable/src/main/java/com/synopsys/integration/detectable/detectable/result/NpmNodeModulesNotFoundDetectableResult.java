package com.synopsys.integration.detectable.detectable.result;

public class NpmNodeModulesNotFoundDetectableResult extends FailedDetectableResult {
    private static final String FORMAT = "A package.json was located in %s, but the node_modules folder was NOT located. Please run 'npm install' in that location and try again.";

    public NpmNodeModulesNotFoundDetectableResult(String directoryPath) {
        super(String.format(FORMAT, directoryPath));
    }
}