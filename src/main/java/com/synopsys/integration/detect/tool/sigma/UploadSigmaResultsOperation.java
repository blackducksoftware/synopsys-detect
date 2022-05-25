package com.synopsys.integration.detect.tool.sigma;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.service.dataservice.SigmaUploadService;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.rest.response.Response;

public class UploadSigmaResultsOperation {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    private final SigmaUploadService sigmaUploadService;

    public UploadSigmaResultsOperation(SigmaUploadService sigmaUploadService) {this.sigmaUploadService = sigmaUploadService;}

    public SigmaUploadResult uploadResults(File resultsFile, String scanId) throws IntegrationException {
        String resultsFileContent;
        try {
            resultsFileContent = FileUtils.readFileToString(resultsFile, Charset.defaultCharset());
        } catch (IOException e) {
            throw new IntegrationException("Unable to parse Sigma results file: " + resultsFile.getAbsolutePath());
        }
        Response response = sigmaUploadService.uploadSigmaResults(resultsFileContent, scanId);
        if (response.isStatusCodeSuccess()) {
            logger.info("Successfully uploaded Sigma results.");
        } else {
            throw new IntegrationException("Failed to upload Sigma results.");
        }
        return new SigmaUploadResult();
    }
}
