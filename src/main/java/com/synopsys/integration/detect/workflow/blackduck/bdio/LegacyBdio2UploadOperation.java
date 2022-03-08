package com.synopsys.integration.detect.workflow.blackduck.bdio;

import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationData;
import com.synopsys.integration.blackduck.codelocation.bdio2legacy.Bdio2UploadService;
import com.synopsys.integration.blackduck.codelocation.upload.UploadBatch;
import com.synopsys.integration.blackduck.codelocation.upload.UploadBatchOutput;
import com.synopsys.integration.exception.IntegrationException;

public class LegacyBdio2UploadOperation extends BdioUploadOperation {
    private final Bdio2UploadService bdioUploadService;

    public LegacyBdio2UploadOperation(Bdio2UploadService bdioUploadService) {
        this.bdioUploadService = bdioUploadService;
    }

    @Override
    protected CodeLocationCreationData<UploadBatchOutput> executeUpload(UploadBatch uploadBatch) throws IntegrationException {
        return bdioUploadService.uploadBdio(uploadBatch);
    }
}
