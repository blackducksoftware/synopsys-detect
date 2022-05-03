package com.synopsys.integration.detect.workflow.blackduck.bdio;

import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationData;
import com.synopsys.integration.blackduck.codelocation.intelligentpersistence.IntelligentPersistenceService;
import com.synopsys.integration.blackduck.codelocation.upload.UploadBatch;
import com.synopsys.integration.blackduck.codelocation.upload.UploadBatchOutput;
import com.synopsys.integration.exception.IntegrationException;

public class IntelligentPersistentUploadOperation extends BdioUploadOperation {
    private final IntelligentPersistenceService intelligentPersistenceService;
    private final Long timeout;

    public IntelligentPersistentUploadOperation(IntelligentPersistenceService intelligentPersistenceService, Long timeout) {
        this.intelligentPersistenceService = intelligentPersistenceService;
        this.timeout = timeout;
    }

    @Override
    protected CodeLocationCreationData<UploadBatchOutput> executeUpload(UploadBatch uploadBatch) throws IntegrationException {
        return intelligentPersistenceService.uploadBdio(uploadBatch, timeout);
    }
}
