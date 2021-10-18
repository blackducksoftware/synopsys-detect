/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.blackduck.developer;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.codelocation.upload.UploadBatch;
import com.synopsys.integration.blackduck.codelocation.upload.UploadTarget;
import com.synopsys.integration.detect.workflow.bdio.BdioResult;
import com.synopsys.integration.detect.workflow.blackduck.developer.blackduck.DetectRapidScanService;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.rest.HttpUrl;

public class RapidModeUploadOperation {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final DetectRapidScanService rapidScanService;

    public RapidModeUploadOperation(DetectRapidScanService rapidScanService) {
        this.rapidScanService = rapidScanService;
    }

    public List<HttpUrl> run(BdioResult bdioResult, @Nullable File rapidScanConfig) throws IntegrationException, IOException {
        logger.info("Begin Rapid Mode Scan");
        UploadBatch uploadBatch = new UploadBatch();
        for (UploadTarget uploadTarget : bdioResult.getUploadTargets()) {
            logger.debug(String.format("Uploading %s", uploadTarget.getUploadFile().getName()));
            uploadBatch.addUploadTarget(uploadTarget);
        }
        List<HttpUrl> results = rapidScanService.performUpload(uploadBatch, rapidScanConfig);
        logger.debug("Rapid scan url count: {}", results.size());
        return results;
    }
}
