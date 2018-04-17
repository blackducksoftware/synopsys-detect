package com.blackducksoftware.integration.hub.detect.bomtool;

import java.util.List;

import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation;

public class BomToolExtractionResult {

    public List<DetectCodeLocation> extractedCodeLocations;
    public BomToolType bomToolType;
    public String directory;
    public Boolean success;
    public Exception exception;

    public BomToolExtractionResult(final List<DetectCodeLocation> extractedCodeLocations, final BomToolType bomToolType, final String directory, final Boolean success, final Exception exception) {
        this.extractedCodeLocations = extractedCodeLocations;
        this.bomToolType = bomToolType;
        this.directory = directory;
        this.success = success;
        this.exception = exception;
    }


}
