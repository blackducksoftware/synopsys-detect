package com.blackducksoftware.integration.hub.detect.bomtool.packagist;

import java.io.File;

import com.blackducksoftware.integration.hub.detect.bomtool.BomToolApplicableResult;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;

public class PackagistApplicableResult extends BomToolApplicableResult {
    private final File composerLock;
    private final File composerJson;

    public PackagistApplicableResult(final File directory, final File composerLock, final File composerJson) {
        super(directory, BomToolType.PACKAGIST);
        this.composerLock = composerLock;
        this.composerJson = composerJson;
    }

    public File getComposerLock() {
        return composerLock;
    }

    public File getComposerJson() {
        return composerJson;
    }

}
