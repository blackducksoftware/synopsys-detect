package com.synopsys.integration.detectable.detectable.result;

public class SetupToolsNoDependenciesDetectableResult extends FailedDetectableResult {

    @Override
    public String toDescription() {
        return "Did not find package dependencies in the pyproject.toml, setup.cfg, or setup.py files. Setuptools Detector will not be run.";
    }
}