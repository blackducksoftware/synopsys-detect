package com.blackduck.integration.detect.tool.binaryscanner;

import java.io.File;
import java.io.IOException;

import com.blackduck.integration.detect.configuration.enumeration.ExitCodeType;
import com.blackduck.integration.detect.lifecycle.run.data.BlackDuckRunData;
import com.blackduck.integration.detect.workflow.codelocation.CodeLocationNameManager;
import com.blackduck.integration.detect.workflow.status.Status;
import com.blackduck.integration.detect.workflow.status.StatusEventPublisher;
import com.blackduck.integration.detect.workflow.status.StatusType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.synopsys.blackduck.upload.client.UploaderConfig;
import com.synopsys.blackduck.upload.client.model.BinaryScanRequestData;
import com.synopsys.blackduck.upload.client.uploaders.BinaryUploader;
import com.synopsys.blackduck.upload.client.uploaders.UploaderFactory;
import com.synopsys.blackduck.upload.rest.status.BinaryUploadStatus;
import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationData;
import com.synopsys.integration.blackduck.codelocation.Result;
import com.synopsys.integration.blackduck.codelocation.binaryscanner.BinaryScan;
import com.synopsys.integration.blackduck.codelocation.binaryscanner.BinaryScanBatch;
import com.synopsys.integration.blackduck.codelocation.binaryscanner.BinaryScanBatchOutput;
import com.synopsys.integration.blackduck.codelocation.binaryscanner.BinaryScanOutput;
import com.synopsys.integration.blackduck.codelocation.binaryscanner.BinaryScanUploadService;
import com.synopsys.integration.blackduck.exception.BlackDuckIntegrationException;
import com.blackduck.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.log.Slf4jIntLogger;
import com.synopsys.integration.util.NameVersion;

public class BinaryUploadOperation {
    private static final String STATUS_KEY = "BINARY_SCAN";
    private static final String BINARY_UPLOAD_FAILURE_MESSAGE = "Failed to upload binary scan file.";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final StatusEventPublisher statusEventPublisher;

    public BinaryUploadOperation(StatusEventPublisher statusEventPublisher) {
        this.statusEventPublisher = statusEventPublisher;
    }

    public BinaryUploadStatus uploadBinaryScanFile(
        File binaryScanFile,
        NameVersion projectNameVersion, 
        CodeLocationNameManager codeLocationNameManager,
        BlackDuckRunData blackDuckRunData
    )
        throws DetectUserFriendlyException {
        
        try {
            BinaryUploader binaryUploader = 
                    createMultipartBinaryScanUploader(binaryScanFile, projectNameVersion, blackDuckRunData, codeLocationNameManager);
            
            logger.info("Preparing to upload binary scan file: " + binaryScanFile.getAbsolutePath());       

            BinaryUploadStatus status = binaryUploader.upload(binaryScanFile.toPath());
            
            if (status.isError()) {
                handleUploadError(status);
            }
                    
            logger.info("Successfully uploaded binary scan file: " + binaryScanFile.getAbsolutePath());
            statusEventPublisher.publishStatusSummary(new Status(STATUS_KEY, StatusType.SUCCESS));
            return status;
        } catch (IntegrationException | IOException e) {
            statusEventPublisher.publishStatusSummary(new Status(STATUS_KEY, StatusType.FAILURE));
            throw new DetectUserFriendlyException(BINARY_UPLOAD_FAILURE_MESSAGE, e, ExitCodeType.FAILURE_BLACKDUCK_FEATURE_ERROR);
        }
    }

    private void handleUploadError(BinaryUploadStatus status) throws DetectUserFriendlyException {
        statusEventPublisher.publishStatusSummary(new Status(STATUS_KEY, StatusType.FAILURE));
        
        IntegrationException exception = null;
        if (status.getException().isPresent()) {
            exception = status.getException().get();      
        }
        
        throw new DetectUserFriendlyException(BINARY_UPLOAD_FAILURE_MESSAGE, exception, ExitCodeType.FAILURE_BLACKDUCK_FEATURE_ERROR);
    }
    
    private BinaryUploader createMultipartBinaryScanUploader(File binaryUpload, NameVersion projectNameVersion,
            BlackDuckRunData blackDuckRunData, CodeLocationNameManager codeLocationNameManager) throws IntegrationException {
        String codeLocationName = codeLocationNameManager.createBinaryScanCodeLocationName(binaryUpload,
                projectNameVersion.getName(), projectNameVersion.getVersion());

        UploaderConfig.Builder uploaderConfigBuilder =  UploaderConfig.createConfigFromEnvironment(
                blackDuckRunData.getBlackDuckServerConfig().getProxyInfo())
                .setBlackDuckTimeoutInSeconds(blackDuckRunData.getBlackDuckServerConfig().getTimeout())
                .setMultipartUploadTimeoutInMinutes(blackDuckRunData.getBlackDuckServerConfig().getTimeout() /  60)
                .setAlwaysTrustServerCertificate(blackDuckRunData.getBlackDuckServerConfig().isAlwaysTrustServerCertificate())
                .setBlackDuckUrl(blackDuckRunData.getBlackDuckServerConfig().getBlackDuckUrl())
                .setApiToken(blackDuckRunData.getBlackDuckServerConfig().getApiToken().get());

        UploaderConfig uploaderConfig = uploaderConfigBuilder.build();
        UploaderFactory uploadFactory = new UploaderFactory(uploaderConfig, new Slf4jIntLogger(logger), new Gson());

        BinaryScanRequestData binaryData = new BinaryScanRequestData(projectNameVersion.getName(),
                projectNameVersion.getVersion(), codeLocationName, "");

        return uploadFactory.createBinaryUploader("/api/uploads", binaryData);
    }
    
    public CodeLocationCreationData<BinaryScanBatchOutput> uploadLegacyBinaryScanFile(
            File binaryScanFile,
            BinaryScanUploadService binaryScanUploadService,
            CodeLocationNameManager codeLocationNameManager,
            NameVersion projectNameVersion
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
                CodeLocationCreationData<BinaryScanBatchOutput> codeLocationCreationData = binaryScanUploadService.uploadBinaryScan(binaryScanBatch);

                BinaryScanBatchOutput binaryScanBatchOutput = codeLocationCreationData.getOutput();
                // The throwExceptionForError() in BinaryScanBatchOutput has a bug, so doing that work here
                throwExceptionForError(binaryScanBatchOutput);

                logger.info("Successfully uploaded binary scan file: " + binaryScanFile.getAbsolutePath());
                statusEventPublisher.publishStatusSummary(new Status(STATUS_KEY, StatusType.SUCCESS));
                return codeLocationCreationData;
            } catch (IntegrationException e) {
                statusEventPublisher.publishStatusSummary(new Status(STATUS_KEY, StatusType.FAILURE));
                throw new DetectUserFriendlyException(BINARY_UPLOAD_FAILURE_MESSAGE, e, ExitCodeType.FAILURE_BLACKDUCK_FEATURE_ERROR);
            }
        }

        public CodeLocationCreationData<BinaryScanBatchOutput> uploadBinaryScanFiles(
            BinaryScanBatch binaryScanBatch,
            BinaryScanUploadService binaryScanUploadService,
            NameVersion projectNameVersion
        )
            throws DetectUserFriendlyException {
            try {
                CodeLocationCreationData<BinaryScanBatchOutput> codeLocationCreationData = binaryScanUploadService.uploadBinaryScan(binaryScanBatch);

                BinaryScanBatchOutput binaryScanBatchOutput = codeLocationCreationData.getOutput();
                // The throwExceptionForError() in BinaryScanBatchOutput has a bug, so doing that work here
                throwExceptionForError(binaryScanBatchOutput);
                statusEventPublisher.publishStatusSummary(new Status(STATUS_KEY, StatusType.SUCCESS));
                return codeLocationCreationData;
            } catch (IntegrationException e) {
                statusEventPublisher.publishStatusSummary(new Status(STATUS_KEY, StatusType.FAILURE));
                throw new DetectUserFriendlyException(BINARY_UPLOAD_FAILURE_MESSAGE, e, ExitCodeType.FAILURE_BLACKDUCK_FEATURE_ERROR);
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