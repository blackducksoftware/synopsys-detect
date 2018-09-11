package com.blackducksoftware.integration.hub.detect.workflow.search;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocation;
import com.synopsys.integration.util.ExcludedIncludedFilter;

public class SearchOptions {
    public File searchPath;
    public final List<String> excludedDirectories;
    public final Boolean forceNestedSearch;
    public final int maxDepth;
    public final ExcludedIncludedFilter bomToolFilter;

    public SearchOptions(DetectConfiguration detectConfiguration){
        searchPath = new File(detectConfiguration.getProperty(DetectProperty.DETECT_SOURCE_PATH));
        excludedDirectories = Arrays.asList(detectConfiguration.getStringArrayProperty(DetectProperty.DETECT_BOM_TOOL_SEARCH_EXCLUSION));
        forceNestedSearch = detectConfiguration.getBooleanProperty(DetectProperty.DETECT_BOM_TOOL_SEARCH_CONTINUE);
        maxDepth = detectConfiguration.getIntegerProperty(DetectProperty.DETECT_BOM_TOOL_SEARCH_DEPTH);
        bomToolFilter = new ExcludedIncludedFilter(detectConfiguration.getProperty(DetectProperty.DETECT_EXCLUDED_BOM_TOOL_TYPES).toUpperCase(), detectConfiguration.getProperty(DetectProperty.DETECT_INCLUDED_BOM_TOOL_TYPES).toUpperCase());
    }
}
