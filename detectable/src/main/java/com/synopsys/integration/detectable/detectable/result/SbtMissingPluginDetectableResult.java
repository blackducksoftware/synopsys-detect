package com.synopsys.integration.detectable.detectable.result;

public class SbtMissingPluginDetectableResult extends FailedDetectableResult {
    private final String directoryPath;

    public SbtMissingPluginDetectableResult(String directoryPath) {
        this.directoryPath = directoryPath;
    }

    @Override
    public String toDescription() {
        return String.format(
            "A build sbt file was located in %s, but no suitable dependency plugin was found. Please ensure you have a suitable plugin installed for this project.",
            directoryPath
        );
    }
}