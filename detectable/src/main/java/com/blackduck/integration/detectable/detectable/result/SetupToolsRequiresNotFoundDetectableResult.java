package com.blackduck.integration.detectable.detectable.result;

public class SetupToolsRequiresNotFoundDetectableResult extends FailedDetectableResult {

    @Override
    public String toDescription() {
        return "Setuptools requires a pyproject.toml with a requires setuptools line in the build-system section. Unable to continue the Setuptools Detector.";
    }
}