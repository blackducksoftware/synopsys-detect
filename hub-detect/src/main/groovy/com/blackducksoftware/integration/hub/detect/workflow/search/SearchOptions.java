package com.blackducksoftware.integration.hub.detect.workflow.search;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.configuration.PropertyAuthority;
import com.synopsys.integration.util.ExcludedIncludedFilter;

public class SearchOptions {
    public File searchPath;
    public final List<String> excludedDirectories;
    public final Boolean forceNestedSearch;
    public final int maxDepth;
    public final ExcludedIncludedFilter bomToolFilter;

    public SearchOptions(DetectConfiguration detectConfiguration) {
        searchPath = new File(detectConfiguration.getProperty(DetectProperty.DETECT_SOURCE_PATH, PropertyAuthority.None));
        excludedDirectories = Arrays.asList(detectConfiguration.getStringArrayProperty(DetectProperty.DETECT_BOM_TOOL_SEARCH_EXCLUSION, PropertyAuthority.None));
        forceNestedSearch = detectConfiguration.getBooleanProperty(DetectProperty.DETECT_BOM_TOOL_SEARCH_CONTINUE, PropertyAuthority.None);
        maxDepth = detectConfiguration.getIntegerProperty(DetectProperty.DETECT_BOM_TOOL_SEARCH_DEPTH, PropertyAuthority.None);
        bomToolFilter = new ExcludedIncludedFilter(detectConfiguration.getProperty(DetectProperty.DETECT_EXCLUDED_BOM_TOOL_TYPES, PropertyAuthority.None).toUpperCase(),
            detectConfiguration.getProperty(DetectProperty.DETECT_INCLUDED_BOM_TOOL_TYPES, PropertyAuthority.None).toUpperCase());
    }
}
