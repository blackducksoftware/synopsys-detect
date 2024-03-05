package com.synopsys.integration.detectable.detectable.result;

public class SetupToolsRequiresNotFoundDetectableResult extends FailedDetectableResult {

    @Override
    public String toDescription() {
        return "setuptools requires is missing from build-system section of the pyproject.toml. Unable to continue the Setuptools Detector.";
    }
}