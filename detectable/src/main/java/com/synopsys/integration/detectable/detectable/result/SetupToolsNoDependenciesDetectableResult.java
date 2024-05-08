package com.synopsys.integration.detectable.detectable.result;

public class SetupToolsNoDependenciesDetectableResult extends FailedDetectableResult {

    @Override
    public String toDescription() {
        return "Unable to find package dependencies in the pyproject.toml, setup.cfg, or setup.py files. Unable to continue the Setuptools Detector.";
    }
}