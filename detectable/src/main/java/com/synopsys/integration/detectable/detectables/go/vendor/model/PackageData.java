package com.synopsys.integration.detectable.detectables.go.vendor.model;

import com.synopsys.integration.util.Stringable;

public class PackageData extends Stringable {

    private final String checksumSHA1;
    private final String path;
    private final String revision;
    private final String revisionTime;

    public PackageData(String checksumSHA1, String path, String revision, String revisionTime) {
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
