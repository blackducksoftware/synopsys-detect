package com.blackduck.integration.detect.workflow.blackduck.bdio;

import com.blackduck.integration.detect.lifecycle.run.operation.blackduck.BdioUploadResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationData;
import com.synopsys.integration.blackduck.codelocation.Result;
import com.synopsys.integration.blackduck.codelocation.upload.UploadBatch;
import com.synopsys.integration.blackduck.codelocation.upload.UploadBatchOutput;
import com.synopsys.integration.blackduck.codelocation.upload.UploadOutput;
import com.synopsys.integration.blackduck.codelocation.upload.UploadTarget;
import com.blackduck.integration.detect.configuration.DetectUserFriendlyException;
import com.blackduck.integration.detect.configuration.enumeration.ExitCodeType;
import com.blackduck.integration.detect.workflow.bdio.BdioResult;
import com.synopsys.integration.exception.IntegrationException;

public abstract class BdioUploadOperation { //TODO: Could use Functional Interface.
    private final Logger logger = LoggerFactory.getLogger(BdioUploadOperation.class);

    public BdioUploadResult uploadBdioFiles(BdioResult bdioResult) throws DetectUserFriendlyException {
        UploadBatch uploadBatch = createBatch(bdioResult);
        CodeLocationCreationData<UploadBatchOutput> response;
        try {
            response = executeUpload(uploadBatch);
        } catch (IntegrationException ex) {
            logger.error("Error uploading bdio files", ex);
            throw new DetectUserFriendlyException("Error uploading bdio files", ex, ExitCodeType.FAILURE_BLACKDUCK_FEATURE_ERROR);
        }
        checkForUploadFailure(response);
        return new BdioUploadResult(response);
    }

    protected abstract CodeLocationCreationData<UploadBatchOutput> executeUpload(UploadBatch uploadBatch) throws IntegrationException;

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
                throw new DetectUserFriendlyException(
                    "An error occurred uploading a bdio file.",
                    uploadOutput.getException().orElse(null),
                    ExitCodeType.FAILURE_BLACKDUCK_FEATURE_ERROR
                );
            }
        }
    }
}
