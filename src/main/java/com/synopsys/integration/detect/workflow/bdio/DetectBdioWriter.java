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

    public DetectBdioWriter(SimpleBdioFactory simpleBdioFactory, DetectInfo detectInfo) {
        this.simpleBdioFactory = simpleBdioFactory;
        this.detectInfo = detectInfo;
    }

    public void writeBdioFile(File outputFile, SimpleBdioDocument simpleBdioDocument) throws DetectUserFriendlyException {
        if (outputFile.exists()) {
            boolean deleteSuccess = outputFile.delete();
            logger.debug(String.format("%s deleted: %b", outputFile.getAbsolutePath(), deleteSuccess));
        }

        try {
            String detectVersion = detectInfo.getDetectVersion();
            SpdxCreator detectCreator = SpdxCreator.createToolSpdxCreator("Detect", detectVersion);
            simpleBdioDocument.getBillOfMaterials().creationInfo.setPrimarySpdxCreator(detectCreator);
            simpleBdioFactory.writeSimpleBdioDocumentToFile(outputFile, simpleBdioDocument);
            logger.debug(String.format("BDIO Generated: %s", outputFile.getAbsolutePath()));
        } catch (IOException e) {
            throw new DetectUserFriendlyException(e.getMessage(), e, ExitCodeType.FAILURE_GENERAL_ERROR);
        }
    }
}
