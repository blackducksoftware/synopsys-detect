package com.blackducksoftware.integration.hub.detect.extraction.bomtool.npm;

import java.io.File;

import com.blackducksoftware.integration.hub.detect.extraction.ExtractionContext;

public class NpmLockfileContext extends ExtractionContext {
    public File directory;
    public File lockfile;
}
