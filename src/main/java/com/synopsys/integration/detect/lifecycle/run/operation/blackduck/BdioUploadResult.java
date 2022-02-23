package com.synopsys.integration.detect.lifecycle.run.operation.blackduck;

import java.util.Optional;

import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationData;
import com.synopsys.integration.blackduck.codelocation.upload.UploadBatchOutput;

public class BdioUploadResult { //TODO: Static creation with success/failure?
    private final CodeLocationCreationData<UploadBatchOutput> uploadOutput;

    public BdioUploadResult(CodeLocationCreationData<UploadBatchOutput> uploadOutput) {
        this.uploadOutput = uploadOutput;
    }

    public Optional<CodeLocationCreationData<UploadBatchOutput>> getUploadOutput() {
        return Optional.ofNullable(uploadOutput);
    }
}
