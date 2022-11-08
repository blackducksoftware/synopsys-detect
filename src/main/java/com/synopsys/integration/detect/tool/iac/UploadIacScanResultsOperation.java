package com.synopsys.integration.detect.tool.iac;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.service.dataservice.IacScanUploadService;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.rest.response.Response;

public class UploadIacScanResultsOperation {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    private final IacScanUploadService iacScanUploadService;

    public UploadIacScanResultsOperation(IacScanUploadService iacScanUploadService) {this.iacScanUploadService = iacScanUploadService;}

    public void uploadResults(File resultsFile, String scanId) throws IntegrationException {
        String resultsFileContent;
        try {
            logger.trace("Reading {} using character encoding {}", resultsFile.getAbsolutePath(), StandardCharsets.UTF_8);
            resultsFileContent = FileUtils.readFileToString(resultsFile, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IntegrationException("Unable to parse Iac Scan results file: " + resultsFile.getAbsolutePath(), e);
        }
        Response response = iacScanUploadService.uploadIacScanResults(resultsFileContent, scanId);
        if (response.isStatusCodeSuccess()) {
            logger.info("Successfully uploaded Iac Scan results.");
        } else {
            throw new IntegrationException(String.format("Iac Scan upload failed with code %d: %s", response.getStatusCode(), response.getStatusMessage()));
        }
    }
}
