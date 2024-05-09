package com.synopsys.integration.detectable.detectable.result;

public class SbtMissingPluginDetectableResult extends FailedDetectableResult {
    private static final String FORMAT = "A build sbt file was located in %s, but no suitable dependency plugin was found. Please ensure you have a suitable plugin installed for this project.";

    public SbtMissingPluginDetectableResult(String directoryPath) {
        super(String.format(FORMAT, directoryPath));
    }
}