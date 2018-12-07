package com.synopsys.detect.doctor.extraction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.detector.DetectorException;
import com.synopsys.detect.doctor.diagnosticparser.DetectRunInfo;
import com.synopsys.detect.doctor.logparser.LoggedDetectExtraction;

public class ExtractionHandler {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public void processExtraction(LoggedDetectExtraction extraction, DetectRunInfo detectRunInfo, DetectConfiguration detectConfiguration) throws DetectorException {
        if (extraction.bomToolDescription.equals("NUGET - Solution")) {
            logger.info("OOOO Snap, I know how to handle this.");
            NugetSolutionExtractionDebugger debugger = new NugetSolutionExtractionDebugger();
            debugger.debug(extraction, detectRunInfo, detectConfiguration);

        } else {
            throw new DetectorException("Don't know what to do with this extraction, sorry brah.");
        }

    }
}
