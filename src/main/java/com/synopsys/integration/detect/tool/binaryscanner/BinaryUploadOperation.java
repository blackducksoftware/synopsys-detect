package com.synopsys.integration.detect.tool.binaryscanner;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.blackduck.upload.client.uploaders.BinaryUploader;
import com.synopsys.blackduck.upload.rest.model.response.UploadFinishResponse;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detect.workflow.codelocation.CodeLocationNameManager;
import com.synopsys.integration.detect.workflow.status.Status;
import com.synopsys.integration.detect.workflow.status.StatusEventPublisher;
import com.synopsys.integration.detect.workflow.status.StatusType;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.util.NameVersion;

public class BinaryUploadOperation {
    private static final String STATUS_KEY = "BINARY_SCAN";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // TODO might not need this depends on how I finalize sending the uploader around
    private final CodeLocationNameManager codeLocationNameManager;
    private final StatusEventPublisher statusEventPublisher;

    public BinaryUploadOperation(StatusEventPublisher statusEventPublisher, CodeLocationNameManager codeLocationNameManager) {
        this.codeLocationNameManager = codeLocationNameManager;
        this.statusEventPublisher = statusEventPublisher;
    }

    public UploadFinishResponse uploadBinaryScanFile(
        File binaryScanFile,
        BinaryUploader binaryUploader,
        NameVersion projectNameVersion
    )
        throws DetectUserFriendlyException {
        try {
            logger.info("Preparing to upload binary scan file: " + binaryScanFile.getAbsolutePath());       

            UploadFinishResponse response = binaryUploader.upload(binaryScanFile.toPath());
                    
            logger.info("Successfully uploaded binary scan file: " + binaryScanFile.getAbsolutePath());
            statusEventPublisher.publishStatusSummary(new Status(STATUS_KEY, StatusType.SUCCESS));
            return response;
        } catch (IntegrationException | IOException e) {
            // TODO need to test how the errors look when there is an exception
            statusEventPublisher.publishStatusSummary(new Status(STATUS_KEY, StatusType.FAILURE));
            throw new DetectUserFriendlyException("Failed to upload binary scan file.", e, ExitCodeType.FAILURE_BLACKDUCK_FEATURE_ERROR);
        }
    }
}
