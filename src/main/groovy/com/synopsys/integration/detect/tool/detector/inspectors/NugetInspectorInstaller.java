package com.synopsys.integration.detect.tool.detector.inspectors;

import java.io.File;

import com.synopsys.integration.detectable.detectable.exception.DetectableException;

public interface NugetInspectorInstaller {
    File installDotnetInspector() throws DetectableException;
    File installExeInspector() throws DetectableException;
}
