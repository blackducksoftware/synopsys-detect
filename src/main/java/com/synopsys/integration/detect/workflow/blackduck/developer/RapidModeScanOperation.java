/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.blackduck.developer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.codelocation.upload.UploadBatch;
import com.synopsys.integration.blackduck.codelocation.upload.UploadTarget;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.workflow.bdio.BdioResult;
import com.synopsys.integration.detect.workflow.blackduck.developer.blackduck.DetectRapidScanService;
import com.synopsys.integration.exception.IntegrationException;

public class RapidModeScanOperation {
    public static final int DEFAULT_WAIT_INTERVAL_IN_SECONDS = 1;
    private static final String OPERATION_NAME = "Black Duck Rapid Scan";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final DetectRapidScanService rapidScanService;
    private final Long timeoutInSeconds;

    public RapidModeScanOperation(DetectRapidScanService rapidScanService, Long timeoutInSeconds) {
        this.rapidScanService = rapidScanService;
        this.timeoutInSeconds = timeoutInSeconds;
    }

    public RapidScanResult run(BdioResult bdioResult) throws DetectUserFriendlyException, InterruptedException, IntegrationException {
        logger.info("Begin Rapid Mode Scan");
        UploadBatch uploadBatch = new UploadBatch();
        for (UploadTarget uploadTarget : bdioResult.getUploadTargets()) {
            logger.debug(String.format("Uploading %s", uploadTarget.getUploadFile().getName()));
            uploadBatch.addUploadTarget(uploadTarget);
        }
        RapidScanResult results = rapidScanService.performUpload(uploadBatch, timeoutInSeconds, DEFAULT_WAIT_INTERVAL_IN_SECONDS);
        logger.debug("Rapid scan result count: {}", results.getComponentResultViews().size());
        return results;
    }
}
