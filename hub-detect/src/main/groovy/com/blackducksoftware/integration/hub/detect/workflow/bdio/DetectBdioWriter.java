package com.blackducksoftware.integration.hub.detect.workflow.bdio;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.DetectInfo;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType;
import com.synopsys.integration.hub.bdio.SimpleBdioFactory;
import com.synopsys.integration.hub.bdio.model.SimpleBdioDocument;
import com.synopsys.integration.hub.bdio.model.SpdxCreator;

public class DetectBdioWriter {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private SimpleBdioFactory simpleBdioFactory;
    private DetectInfo detectInfo;

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
            final String hubDetectVersion = detectInfo.getDetectVersion();
            final SpdxCreator hubDetectCreator = SpdxCreator.createToolSpdxCreator("Detect", hubDetectVersion);
            simpleBdioDocument.billOfMaterials.creationInfo.setPrimarySpdxCreator(hubDetectCreator);
            simpleBdioFactory.writeSimpleBdioDocumentToFile(outputFile, simpleBdioDocument);
            logger.info(String.format("BDIO Generated: %s", outputFile.getAbsolutePath()));
        } catch (final IOException e) {
            throw new DetectUserFriendlyException(e.getMessage(), e, ExitCodeType.FAILURE_GENERAL_ERROR);
        }
    }
}
