/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.lifecycle.run.operation.blackduck;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationData;
import com.synopsys.integration.blackduck.codelocation.bdio2legacy.Bdio2UploadService;
import com.synopsys.integration.blackduck.codelocation.bdiolegacy.BdioUploadService;
import com.synopsys.integration.blackduck.codelocation.intelligentpersistence.IntelligentPersistenceService;
import com.synopsys.integration.blackduck.codelocation.upload.UploadBatchOutput;
import com.synopsys.integration.blackduck.codelocation.upload.UploadTarget;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.lifecycle.run.data.BlackDuckRunData;
import com.synopsys.integration.detect.workflow.bdio.BdioOptions;
import com.synopsys.integration.detect.workflow.bdio.BdioResult;
import com.synopsys.integration.detect.workflow.blackduck.DetectBdioUploadService;
import com.synopsys.integration.detect.workflow.status.OperationSystem;
import com.synopsys.integration.exception.IntegrationException;

public class BdioUploadOperation { //TODO: [Operation] Needs to be migrated - currently performs more than one operation.
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final OperationSystem operationSystem;
    private final BdioOptions bdioOptions;

    public BdioUploadOperation(OperationSystem operationSystem, BdioOptions bdioOptions) {
        this.operationSystem = operationSystem;
        this.bdioOptions = bdioOptions;
    }

    public BdioUploadResult execute(BlackDuckRunData blackDuckRunData, BdioResult bdioResult) throws DetectUserFriendlyException, IntegrationException {
        List<UploadTarget> uploadTargetList = bdioResult.getUploadTargets();
        if (!uploadTargetList.isEmpty()) {
            logger.info(String.format("Created %d BDIO files.", bdioResult.getUploadTargets().size()));
            if (blackDuckRunData.isOnline()) {
                logger.debug("Uploading BDIO files.");
                BlackDuckServicesFactory blackDuckServicesFactory = blackDuckRunData.getBlackDuckServicesFactory();
                BdioUploadService bdioUploadService = blackDuckServicesFactory.createBdioUploadService();
                Bdio2UploadService bdio2UploadService = blackDuckServicesFactory.createBdio2UploadService();
                IntelligentPersistenceService intelligentPersistenceScanService = blackDuckServicesFactory.createIntelligentPersistenceService();
                DetectBdioUploadService detectBdioUploadService = new DetectBdioUploadService(operationSystem, bdioOptions);
                CodeLocationCreationData<UploadBatchOutput> uploadResult = detectBdioUploadService.uploadBdioFiles(bdioResult, bdioUploadService, bdio2UploadService, intelligentPersistenceScanService);
                return new BdioUploadResult(uploadResult);
            }
        } else {
            logger.debug("Did not create any BDIO files.");
        }
        return new BdioUploadResult(null);
    }
}
