/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.bdio;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.SimpleBdioFactory;
import com.synopsys.integration.bdio.model.SimpleBdioDocument;
import com.synopsys.integration.bdio.model.SpdxCreator;
import com.synopsys.integration.detect.configuration.DetectInfo;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;

public class DetectBdioWriter {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final SimpleBdioFactory simpleBdioFactory;
    private final DetectInfo detectInfo;

    public DetectBdioWriter(final SimpleBdioFactory simpleBdioFactory, final DetectInfo detectInfo) {
        this.simpleBdioFactory = simpleBdioFactory;
        this.detectInfo = detectInfo;
    }

    public void writeBdioFile(final File outputFile, final SimpleBdioDocument simpleBdioDocument) throws DetectUserFriendlyException {
        if (outputFile.exists()) {
            final boolean deleteSuccess = outputFile.delete();
            logger.debug(String.format("%s deleted: %b", outputFile.getAbsolutePath(), deleteSuccess));
        }

        try {
            final String detectVersion = detectInfo.getDetectVersion();
            final SpdxCreator detectCreator = SpdxCreator.createToolSpdxCreator("Detect", detectVersion);
            simpleBdioDocument.getBillOfMaterials().creationInfo.setPrimarySpdxCreator(detectCreator);
            simpleBdioFactory.writeSimpleBdioDocumentToFile(outputFile, simpleBdioDocument);
            logger.debug(String.format("BDIO Generated: %s", outputFile.getAbsolutePath()));
        } catch (final IOException e) {
            throw new DetectUserFriendlyException(e.getMessage(), e, ExitCodeType.FAILURE_GENERAL_ERROR);
        }
    }
}
