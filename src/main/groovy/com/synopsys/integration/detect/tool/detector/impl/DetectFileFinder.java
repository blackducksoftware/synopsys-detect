package com.synopsys.integration.detect.tool.detector.impl;

import java.io.File;
import java.util.List;

import com.synopsys.integration.detectable.detectable.file.FileFinder;

public class DetectFileFinder implements FileFinder {
    @Override
    public File findFile(final File directoryToSearch, final String filenamePattern) {
        return null;
    }

    @Override
    public List<File> findFiles(final File directoryToSearch, final String filenamePattern) {
        return null;
    }

    @Override
    public List<File> findFiles(final File directoryToSearch, final String filenamePattern, final int depth) {
        return null;
    }
}
