package com.synopsys.integration.detect.tool.detector.inspectors.nuget;

import java.io.File;

import com.synopsys.integration.detectable.detectable.exception.DetectableException;

public interface NugetInspectorLocator {
    File locateDotnet3Inspector() throws DetectableException;

    File locateDotnetInspector() throws DetectableException;

    File locateExeInspector() throws DetectableException;

    File locateDotnet5Inspector() throws DetectableException;
}
