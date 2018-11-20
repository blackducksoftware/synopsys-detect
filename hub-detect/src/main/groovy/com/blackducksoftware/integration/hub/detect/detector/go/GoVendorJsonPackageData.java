package com.blackducksoftware.integration.hub.detect.detector.go;

import com.synopsys.integration.util.Stringable;

public class GoVendorJsonPackageData extends Stringable {

    private final String checksumSHA1;
    private final String path;
    private final String revision;
    private final String revisionTime;

    public GoVendorJsonPackageData(final String checksumSHA1, final String path, final String revision, final String revisionTime) {
        this.checksumSHA1 = checksumSHA1;
        this.path = path;
        this.revision = revision;
        this.revisionTime = revisionTime;
    }

    public String getChecksumSHA1() {
        return checksumSHA1;
    }

    public String getPath() {
        return path;
    }

    public String getRevision() {
        return revision;
    }

    public String getRevisionTime() {
        return revisionTime;
    }
}
