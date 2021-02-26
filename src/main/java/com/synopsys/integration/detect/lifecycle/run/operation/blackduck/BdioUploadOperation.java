/**
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.lifecycle.run.operation.blackduck;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationData;
import com.synopsys.integration.blackduck.codelocation.bdio2upload.Bdio2UploadService;
import com.synopsys.integration.blackduck.codelocation.bdioupload.BdioUploadService;
import com.synopsys.integration.blackduck.codelocation.bdioupload.UploadBatchOutput;
import com.synopsys.integration.blackduck.codelocation.bdioupload.UploadTarget;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.lifecycle.run.data.BlackDuckRunData;
import com.synopsys.integration.detect.workflow.bdio.BdioResult;
import com.synopsys.integration.detect.workflow.blackduck.DetectBdioUploadService;
import com.synopsys.integration.exception.IntegrationException;

public class BdioUploadOperation {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public Optional<CodeLocationCreationData<UploadBatchOutput>> execute(BlackDuckRunData blackDuckRunData, BdioResult bdioResult) throws DetectUserFriendlyException, IntegrationException {
        Optional<CodeLocationCreationData<UploadBatchOutput>> result = Optional.empty();
        List<UploadTarget> uploadTargetList = bdioResult.getUploadTargets();
        if (!uploadTargetList.isEmpty()) {
            logger.info(String.format("Created %d BDIO files.", bdioResult.getUploadTargets().size()));
            if (blackDuckRunData.isOnline()) {
                logger.debug("Uploading BDIO files.");
                BlackDuckServicesFactory blackDuckServicesFactory = blackDuckRunData.getBlackDuckServicesFactory();
                BdioUploadService bdioUploadService = blackDuckServicesFactory.createBdioUploadService();
                Bdio2UploadService bdio2UploadService = blackDuckServicesFactory.createBdio2UploadService();
                DetectBdioUploadService detectBdioUploadService = new DetectBdioUploadService();
                logger.info(String.format("Created %d BDIO files.", uploadTargetList.size()));
                logger.debug("Uploading BDIO files.");
                result = Optional.of(detectBdioUploadService.uploadBdioFiles(bdioResult, bdioUploadService,
                    bdio2UploadService));
            }
        } else {
            logger.debug("Did not create any BDIO files.");
        }
        return result;
    }
}
