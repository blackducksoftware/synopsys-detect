/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.lifecycle.run.operation.blackduck;

import java.util.Optional;

import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationData;
import com.synopsys.integration.blackduck.codelocation.upload.UploadBatchOutput;

public class BdioUploadResult { //TODO: Static creation with success/failure?
    private CodeLocationCreationData<UploadBatchOutput> uploadOutput;

    public BdioUploadResult(final CodeLocationCreationData<UploadBatchOutput> uploadOutput) {
        this.uploadOutput = uploadOutput;
    }

    public Optional<CodeLocationCreationData<UploadBatchOutput>> getUploadOutput() {
        return Optional.ofNullable(uploadOutput);
    }
}
