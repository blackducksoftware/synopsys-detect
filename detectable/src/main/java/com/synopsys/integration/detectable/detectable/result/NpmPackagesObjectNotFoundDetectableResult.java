package com.synopsys.integration.detectable.detectable.result;

public class NpmPackagesObjectNotFoundDetectableResult extends FailedDetectableResult {

    @Override
    public String toDescription() {
        return "No packages object was found. This may be due to a package-lock.json or npm-shirnkwrap.json file created by an earlier version of npm. Please run 'npm install' with a supported version of npm and try again.";
    }
}