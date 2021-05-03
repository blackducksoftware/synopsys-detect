/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.blackduck;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationData;
import com.synopsys.integration.blackduck.codelocation.Result;
import com.synopsys.integration.blackduck.codelocation.bdio2legacy.Bdio2UploadService;
import com.synopsys.integration.blackduck.codelocation.bdiolegacy.BdioUploadService;
import com.synopsys.integration.blackduck.codelocation.intelligentpersistence.IntelligentPersistenceService;
import com.synopsys.integration.blackduck.codelocation.upload.UploadBatch;
import com.synopsys.integration.blackduck.codelocation.upload.UploadBatchOutput;
import com.synopsys.integration.blackduck.codelocation.upload.UploadOutput;
import com.synopsys.integration.blackduck.codelocation.upload.UploadTarget;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detect.workflow.bdio.BdioOptions;
import com.synopsys.integration.detect.workflow.bdio.BdioResult;
import com.synopsys.integration.detect.workflow.status.OperationSystem;
import com.synopsys.integration.exception.IntegrationException;

public class DetectBdioUploadService {
    private static final String OPERATION_NAME = "Black Duck BDIO Upload";
    private final Logger logger = LoggerFactory.getLogger(DetectBdioUploadService.class);
    private final OperationSystem operationSystem;
    private final BdioOptions bdioOptions;

    public DetectBdioUploadService(OperationSystem operationSystem, BdioOptions bdioOptions) {
        this.operationSystem = operationSystem;
        this.bdioOptions = bdioOptions;
    }

    public CodeLocationCreationData<UploadBatchOutput> uploadBdioFiles(BdioResult bdioResult, BdioUploadService bdioUploadService, Bdio2UploadService bdio2UploadService,
        IntelligentPersistenceService intelligentPersistenceScanService) throws DetectUserFriendlyException, IntegrationException {

        UploadBatch uploadBatch = createBatch(bdioResult);
        CodeLocationCreationData<UploadBatchOutput> response;
        try {
            if (bdioOptions.isLegacyUploadEnabled()) {
                response = legacyUpload(bdioResult, uploadBatch, bdioUploadService, bdio2UploadService);
            } else {
                logger.debug("Performing intelligent BDIO upload.");
                response = intelligentPersistenceScanService.uploadBdio(uploadBatch);
            }
        } catch (IntegrationException ex) {
            logger.error("Error uploading bdio files", ex);
            operationSystem.completeWithError(OPERATION_NAME, ex.getMessage());
            throw ex;
        }
        checkForUploadFailure(response);
        operationSystem.completeWithSuccess(OPERATION_NAME);

        return response;
    }

    private CodeLocationCreationData<UploadBatchOutput> legacyUpload(BdioResult bdioResult, UploadBatch uploadBatch, BdioUploadService bdioUploadService, Bdio2UploadService bdio2UploadService) throws IntegrationException {
        CodeLocationCreationData<UploadBatchOutput> response;
        logger.debug("Performing legacy BDIO upload.");
        if (bdioResult.isBdio2()) {
            response = bdio2UploadService.uploadBdio(uploadBatch);
        } else {
            response = bdioUploadService.uploadBdio(uploadBatch);
        }
        return response;
    }

    private UploadBatch createBatch(BdioResult bdioResult) {
        UploadBatch uploadBatch = new UploadBatch();
        for (UploadTarget uploadTarget : bdioResult.getUploadTargets()) {
            logger.debug(String.format("Uploading %s", uploadTarget.getUploadFile().getName()));
            uploadBatch.addUploadTarget(uploadTarget);
        }
        return uploadBatch;
    }

    private void checkForUploadFailure(CodeLocationCreationData<UploadBatchOutput> response) throws DetectUserFriendlyException {
        for (UploadOutput uploadOutput : response.getOutput()) {
            if (uploadOutput.getResult() == Result.FAILURE) {
                logger.error(String.format("Failed to upload code location: %s", uploadOutput.getCodeLocationName()));
                logger.error(String.format("Reason: %s", uploadOutput.getErrorMessage().orElse("Unknown reason.")));
                operationSystem.completeWithError(uploadOutput.getException().map(Exception::getMessage).orElse(""));
                throw new DetectUserFriendlyException("An error occurred uploading a bdio file.", uploadOutput.getException().orElse(null), ExitCodeType.FAILURE_BLACKDUCK_FEATURE_ERROR);
            }
        }
    }
}
