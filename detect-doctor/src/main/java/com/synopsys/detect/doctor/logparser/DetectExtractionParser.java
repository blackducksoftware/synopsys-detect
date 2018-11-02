package com.synopsys.detect.doctor.logparser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DetectExtractionParser {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private String EXTRACTION_NAME_MARKER = "Starting extraction: ";
    private String EXTRACTION_NAME_SPLIT_MARKER = " - ";
    private String EXTRACTION_IDENTIFIER_MARKER = "Identifier: ";
    private String EXTRACTION_PARAMETER_PREFIX = " --- ";
    private String EXTRACTION_PARAMETER_SEPERATOR = " : ";

    public void parseExtractionHeader(LoggedDetectExtraction extraction, String line) {
        extraction.rawHeader.add(line);

        if (line.contains(EXTRACTION_NAME_MARKER)) {
            extraction.bomToolDescription = DoctorStringUtils.substringAfter(line, EXTRACTION_NAME_MARKER);
            String[] descriptionPieces = extraction.bomToolDescription.split(EXTRACTION_NAME_SPLIT_MARKER);
            if (descriptionPieces.length == 2) {
                extraction.bomToolGroup = descriptionPieces[0];
                extraction.bomToolName = descriptionPieces[1];
            } else {
                logger.error("Unable to parse detector information from line: " + line);
            }
        } else if (line.contains(EXTRACTION_IDENTIFIER_MARKER)) {
            extraction.extractionIdentifier = DoctorStringUtils.substringAfter(line, EXTRACTION_IDENTIFIER_MARKER);
        } else {
            String actualLine = DoctorStringUtils.substringAfter(line, EXTRACTION_PARAMETER_PREFIX);
            String[] paramPieces = actualLine.split(EXTRACTION_PARAMETER_SEPERATOR);
            if (paramPieces.length == 2) {
                String name = paramPieces[0];
                String value = paramPieces[1];
                extraction.parameters.put(name, value);
            } else {
                logger.error("Unable to parse parameter information from line: " + line);
            }
        }
    }

    public void parseExtractionBody(LoggedDetectExtraction extraction, String line) {
        extraction.rawBody.add(line);
    }

    public void parseExtractionFooter(LoggedDetectExtraction extraction, String line) {
        extraction.rawFooter.add(line);
    }
}
