package com.blackducksoftware.integration.hub.detect.extraction.bomtool.pip;

import java.io.File;

import com.blackducksoftware.integration.hub.detect.extraction.ExtractionContext;

public class PipInspectorContext  extends ExtractionContext {
    public File directory;
    public String pythonExe;
    public File pipInspector;
    public File setupFile;
    public String requirementFilePath;
}