package com.synopsys.integration.detect.tool.detector;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detect.workflow.codelocation.DetectCodeLocation;
import com.synopsys.integration.detect.workflow.codelocation.FileNameUtils;
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detector.base.DetectorEvaluation;

public class CodeLocationConverter {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ExternalIdFactory externalIdFactory;

    public CodeLocationConverter(final ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public Map<CodeLocation, DetectCodeLocation> toDetectCodeLocation(File detectSourcePath, DetectorEvaluation evaluation){
        Map<CodeLocation, DetectCodeLocation> detectCodeLocations = new HashMap<>();
        if (evaluation.wasExtractionSuccessful()){
            Extraction extraction = evaluation.getExtraction();
            String name = evaluation.getDetectorRule().getDetectorType().toString();
            return toDetectCodeLocation(detectSourcePath, extraction, evaluation.getDetectableEnvironment().getDirectory(), name);
        }
        return detectCodeLocations;
    }

    public Map<CodeLocation, DetectCodeLocation> toDetectCodeLocation(File detectSourcePath, Extraction extraction, File overridePath, String overrideName){
        Map<CodeLocation, DetectCodeLocation> detectCodeLocations = new HashMap<>();

        for (CodeLocation codeLocation : extraction.getCodeLocations()){
            File sourcePath = codeLocation.getSourcePath().orElse(overridePath);
            ExternalId externalId;
            if (!codeLocation.getExternalId().isPresent()){
                logger.warn("The detector was unable to determine an external id for this code location, so an external id will be created using the file path.");
                Forge detectForge = new Forge("/", "/", "Detect");
                final String relativePath = FileNameUtils.relativize(detectSourcePath.getAbsolutePath(), sourcePath.getAbsolutePath());
                externalId = externalIdFactory.createPathExternalId(detectForge, relativePath);
                logger.warn("The external id that was created is: " + externalId.getExternalIdPieces().toString());
            } else {
                externalId = codeLocation.getExternalId().get();
            }
            DetectCodeLocation detectCodeLocation = DetectCodeLocation.forCreator(codeLocation.getDependencyGraph(), sourcePath, externalId, overrideName);
            detectCodeLocations.put(codeLocation, detectCodeLocation);
        }

        return detectCodeLocations;
    }
}
