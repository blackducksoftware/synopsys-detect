/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.blackduck;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationData;
import com.synopsys.integration.blackduck.codelocation.Result;
import com.synopsys.integration.blackduck.codelocation.bdio2upload.Bdio2UploadService;
import com.synopsys.integration.blackduck.codelocation.bdioupload.BdioUploadService;
import com.synopsys.integration.blackduck.codelocation.bdioupload.UploadBatch;
import com.synopsys.integration.blackduck.codelocation.bdioupload.UploadBatchOutput;
import com.synopsys.integration.blackduck.codelocation.bdioupload.UploadOutput;
import com.synopsys.integration.blackduck.codelocation.bdioupload.UploadTarget;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detect.workflow.bdio.BdioResult;
import com.synopsys.integration.detect.workflow.status.DetectIssue;
import com.synopsys.integration.detect.workflow.status.DetectIssueType;
import com.synopsys.integration.detect.workflow.status.Status;
import com.synopsys.integration.detect.workflow.status.StatusEventPublisher;
import com.synopsys.integration.detect.workflow.status.StatusType;
import com.synopsys.integration.exception.IntegrationException;

public class DetectBdioUploadService {
    private static final String STATUS_KEY = "BLACK_DUCK_BDIO_UPLOAD";
    private final Logger logger = LoggerFactory.getLogger(DetectBdioUploadService.class);
    private final StatusEventPublisher statusEventPublisher;

    public DetectBdioUploadService(StatusEventPublisher statusEventPublisher) {
        this.statusEventPublisher = statusEventPublisher;
    }

    public CodeLocationCreationData<UploadBatchOutput> uploadBdioFiles(BdioResult bdioResult, BdioUploadService bdioUploadService, Bdio2UploadService bdio2UploadService) throws DetectUserFriendlyException, IntegrationException {
        UploadBatch uploadBatch = new UploadBatch();
        for (UploadTarget uploadTarget : bdioResult.getUploadTargets()) {
            logger.debug(String.format("Uploading %s", uploadTarget.getUploadFile().getName()));
            uploadBatch.addUploadTarget(uploadTarget);
        }

        CodeLocationCreationData<UploadBatchOutput> response;
        try {
            if (bdioResult.isBdio2()) {
                response = bdio2UploadService.uploadBdio(uploadBatch);
            } else {
                response = bdioUploadService.uploadBdio(uploadBatch);
            }
        } catch (IntegrationException ex) {
            logger.error("Error uploading bdio files", ex);
            statusEventPublisher.publishStatusSummary(new Status(STATUS_KEY, StatusType.FAILURE));
            statusEventPublisher.publishIssue(new DetectIssue(DetectIssueType.EXCEPTION, Arrays.asList(ex.getMessage())));
            throw ex;
        }

        for (UploadOutput uploadOutput : response.getOutput()) {
            if (uploadOutput.getResult() == Result.FAILURE) {
                logger.error(String.format("Failed to upload code location: %s", uploadOutput.getCodeLocationName()));
                logger.error(String.format("Reason: %s", uploadOutput.getErrorMessage().orElse("Unknown reason.")));
                statusEventPublisher.publishStatusSummary(new Status(STATUS_KEY, StatusType.FAILURE));
                throw new DetectUserFriendlyException("An error occurred uploading a bdio file.", uploadOutput.getException().orElse(null), ExitCodeType.FAILURE_BLACKDUCK_FEATURE_ERROR);
            }
        }
        statusEventPublisher.publishStatusSummary(new Status(STATUS_KEY, StatusType.SUCCESS));

        return response;
    }

}
