package com.blackducksoftware.integration.hub.detect.extraction.bomtool.npm;

import java.io.File;

import com.blackducksoftware.integration.hub.detect.extraction.ExtractionContext;

public class NpmCliContext extends ExtractionContext {
    public String npmExe;
    public File nodeModules;
    public File packageJson;
}
