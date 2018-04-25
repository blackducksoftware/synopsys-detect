package com.blackducksoftware.integration.hub.detect.extraction.bomtool.npm;

import com.blackducksoftware.integration.hub.detect.extraction.bucket.BucketValue;

public class NpmCliContext {

    public static final String NODE_MODULES_KEY = "nodeModules";
    public static final String NPM_EXE_KEY = "npmExe";
    public static final String PACKAGE_JSON_KEY = "packageJson";

    @BucketValue(NPM_EXE_KEY)
    public String npmExe;

    @BucketValue(NODE_MODULES_KEY)
    public String nodeModules;

    @BucketValue(PACKAGE_JSON_KEY)
    public String packageJson;

}
