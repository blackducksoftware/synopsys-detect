package com.blackducksoftware.integration.hub.detect.extraction.bomtool.npm;

import com.blackducksoftware.integration.hub.detect.extraction.bucket.BucketValue;

public class NpmPackageLockContext {
    public static final String PACKAGELOCK_KEY = "packagelock";

    @BucketValue(PACKAGELOCK_KEY)
    public String packageLock;
}
