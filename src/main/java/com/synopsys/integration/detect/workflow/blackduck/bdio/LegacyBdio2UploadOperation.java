/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.blackduck.bdio;

import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationData;
import com.synopsys.integration.blackduck.codelocation.bdio2legacy.Bdio2UploadService;
import com.synopsys.integration.blackduck.codelocation.upload.UploadBatch;
import com.synopsys.integration.blackduck.codelocation.upload.UploadBatchOutput;
import com.synopsys.integration.exception.IntegrationException;

public class LegacyBdio2UploadOperation extends BdioUploadOperation {
    private Bdio2UploadService bdioUploadService;

    public LegacyBdio2UploadOperation(final Bdio2UploadService bdioUploadService) {
        this.bdioUploadService = bdioUploadService;
    }

    @Override
    protected CodeLocationCreationData<UploadBatchOutput> executeUpload(final UploadBatch uploadBatch) throws IntegrationException {
        return bdioUploadService.uploadBdio(uploadBatch);
    }
}
