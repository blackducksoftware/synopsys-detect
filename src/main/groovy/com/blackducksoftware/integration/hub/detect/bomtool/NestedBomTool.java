package com.blackducksoftware.integration.hub.detect.bomtool;

import java.util.List;

import com.blackducksoftware.integration.hub.detect.bomtool.search.BomToolSearchResult;
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation;

public interface NestedBomTool<T extends BomToolSearchResult> {
    public List<DetectCodeLocation> extractDetectCodeLocations(T bomToolSearchResult);

}
