package com.blackducksoftware.integration.hub.detect.extraction.bomtool.npm;

import com.blackducksoftware.integration.hub.detect.extraction.bucket.BucketValue;

public class NpmShrinkwrapContext {

    public static final String SHRINKWRAP_KEY = "shrinkwrap";

    @BucketValue(SHRINKWRAP_KEY)
    public String shrinkwrap;
}
