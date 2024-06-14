package com.synopsys.integration.detect.tool.binaryscanner;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.synopsys.blackduck.upload.client.UploaderConfig;
import com.synopsys.blackduck.upload.client.model.BinaryScanRequestData;
import com.synopsys.blackduck.upload.client.uploaders.BinaryUploader;
import com.synopsys.blackduck.upload.client.uploaders.UploaderFactory;
import com.synopsys.blackduck.upload.rest.model.response.UploadFinishResponse;
import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationData;
import com.synopsys.integration.blackduck.codelocation.Result;
import com.synopsys.integration.blackduck.codelocation.binaryscanner.BinaryScan;
import com.synopsys.integration.blackduck.codelocation.binaryscanner.BinaryScanBatch;
import com.synopsys.integration.blackduck.codelocation.binaryscanner.BinaryScanBatchOutput;
import com.synopsys.integration.blackduck.codelocation.binaryscanner.BinaryScanOutput;
import com.synopsys.integration.blackduck.codelocation.binaryscanner.BinaryScanUploadService;
import com.synopsys.integration.blackduck.exception.BlackDuckIntegrationException;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detect.workflow.codelocation.CodeLocationNameManager;
import com.synopsys.integration.detect.workflow.status.Status;
import com.synopsys.integration.detect.workflow.status.StatusEventPublisher;
import com.synopsys.integration.detect.workflow.status.StatusType;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.log.Slf4jIntLogger;
import com.synopsys.integration.util.NameVersion;

public class BinaryUploadOperation {
    private static final String STATUS_KEY = "BINARY_SCAN";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final CodeLocationNameManager codeLocationNameManager;
    private final StatusEventPublisher statusEventPublisher;

    public BinaryUploadOperation(StatusEventPublisher statusEventPublisher, CodeLocationNameManager codeLocationNameManager) {
        this.codeLocationNameManager = codeLocationNameManager;
        this.statusEventPublisher = statusEventPublisher;
    }

    public UploadFinishResponse uploadBinaryScanFile(
        File binaryScanFile,
        BinaryScanUploadService binaryScanUploadService,
        NameVersion projectNameVersion, BinaryUploader binaryUploader
    )
        throws DetectUserFriendlyException {
        String codeLocationName = codeLocationNameManager.createBinaryScanCodeLocationName(
            binaryScanFile,
            projectNameVersion.getName(),
            projectNameVersion.getVersion()
        );
        try {
            logger.info("Preparing to upload binary scan file: " + binaryScanFile.getAbsolutePath());
            BinaryScan binaryScan = new BinaryScan(binaryScanFile, projectNameVersion.getName(), projectNameVersion.getVersion(), codeLocationName);
            BinaryScanBatch binaryScanBatch = new BinaryScanBatch(binaryScan);
            
            // TODO likely put library here as we have the single zip in binaryUpload and we have the codeLocationNameManager here.
            // TODO if we need the code location name we can likely do
                    // TODO this only has etag and location. So how do I tell if something fails? Exception?
                    // Just print the library logging to the logs?
            
            // TODO handle two of these?
                    UploadFinishResponse response = binaryUploader.upload(binaryScanFile.toPath());
                    
                    logger.info("Successfully uploaded binary scan file: " + binaryScanFile.getAbsolutePath());
                    statusEventPublisher.publishStatusSummary(new Status(STATUS_KEY, StatusType.SUCCESS));
                
           // CodeLocationCreationData<BinaryScanBatchOutput> codeLocationCreationData = binaryScanUploadService.uploadBinaryScan(binaryScanBatch);

         //   BinaryScanBatchOutput binaryScanBatchOutput = codeLocationCreationData.getOutput();
            // The throwExceptionForError() in BinaryScanBatchOutput has a bug, so doing that work here
          //  throwExceptionForError(binaryScanBatchOutput);
            return response;
        } catch (IntegrationException | IOException e) {
            statusEventPublisher.publishStatusSummary(new Status(STATUS_KEY, StatusType.FAILURE));
            throw new DetectUserFriendlyException("Failed to upload binary scan file.", e, ExitCodeType.FAILURE_BLACKDUCK_FEATURE_ERROR);
        }
    }

    // BinaryScanBatchOutput used to do this, but our understanding of what needs to happen has been
    // changing rapidly. Once we're confident we know what it should do, it should presumably move back there.
    private void throwExceptionForError(BinaryScanBatchOutput binaryScanBatchOutput) throws BlackDuckIntegrationException {
        for (BinaryScanOutput binaryScanOutput : binaryScanBatchOutput) {
            if (binaryScanOutput.getResult() == Result.FAILURE) {
                // Black Duck responses are single-line message (loggable as-is), but nginx Bad Gateway responses are
                // multi-line html with the message embedded (that mess up the log).
                // cleanResponse() attempts to produce something reasonable to log in either case
                String cleanedBlackDuckResponse = cleanResponse(binaryScanOutput.getResponse());
                String uploadErrorMessage = String.format(
                    "Error when uploading binary scan: %s (Black Duck response: %s)",
                    binaryScanOutput.getErrorMessage().orElse(binaryScanOutput.getStatusMessage()),
                    cleanedBlackDuckResponse
                );
                logger.error(uploadErrorMessage);
                throw new BlackDuckIntegrationException(uploadErrorMessage);
            }
        }
    }

    private String cleanResponse(String response) {
        if (StringUtils.isBlank(response)) {
            return "";
        }
        String lengthLimitedResponse = StringUtils.substring(response, 0, 200);
        if (!lengthLimitedResponse.equals(response)) {
            lengthLimitedResponse = lengthLimitedResponse + "...";
        }
        String[] searchList = { "\n", "\r" };
        String[] replacementList = { " ", "" };
        return StringUtils.replaceEachRepeatedly(lengthLimitedResponse, searchList, replacementList);
    }
}
