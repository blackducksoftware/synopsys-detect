package com.blackducksoftware.integration.hub.detect.extraction.bomtool.packagist;

import java.io.File;

import com.blackducksoftware.integration.hub.detect.extraction.ExtractionContext;

public class ComposerLockContext extends ExtractionContext {
    public File directory;
    public File composerJson;
    public File composerLock;
}