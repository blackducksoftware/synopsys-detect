package com.synopsys.integration.detect.tool.detector.executable;

import java.io.File;

import com.blackduck.integration.detectable.detectable.exception.DetectableException;

@FunctionalInterface
public interface ExecutableResolverFunction {
    File resolve() throws DetectableException;
}
