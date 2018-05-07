package com.blackducksoftware.integration.hub.detect.extraction;

import java.util.ArrayList;
import java.util.List;

import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation;

public class Extraction {

    public List<DetectCodeLocation> codeLocations = new ArrayList<>();
    public ExtractionResult result;
    public Exception error;
    public String description;

    public Extraction(final ExtractionResult result, final DetectCodeLocation codeLocation) {
        this.codeLocations.add(codeLocation);
        this.result = result;
    }

    public Extraction(final ExtractionResult result, final List<DetectCodeLocation> codeLocations) {
        this.codeLocations = codeLocations;
        this.result = result;
    }

    public Extraction(final ExtractionResult result) {
        this.result = result;
    }

    public Extraction(final ExtractionResult result, final String description) {
        this.result = result;
        this.description = description;
    }

    public Extraction(final ExtractionResult result, final Exception error) {
        this.result = result;
        this.error = error;
    }


    public enum ExtractionResult {
        Success,
        Failure,
        Exception
    }

}
