package com.blackducksoftware.integration.hub.detect.bomtool;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation;

@Component
public class BomToolExtractionResultsFactory {

    public BomToolExtractionResult fromException(final BomToolType type, final String directory, final Exception e) {
        return new BomToolExtractionResult(new ArrayList<>(), type, directory, false, e);
    }

    public BomToolExtractionResult fromCodeLocations(final List<DetectCodeLocation> codeLocations, final BomToolType type, final String directory) {
        return new BomToolExtractionResult(codeLocations, type, directory, true, null);
    }

    public BomToolExtractionResult fromCodeLocations(final List<DetectCodeLocation> codeLocations, final BomToolType type, final File directory) {
        return new BomToolExtractionResult(codeLocations, type, directory.toString(), true, null);
    }

}
