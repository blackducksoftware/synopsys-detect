package com.blackducksoftware.integration.hub.detect.workflow.search;

import java.io.File;

public interface DetectorSearchFilter {
    boolean shouldExclude(File file);
}
