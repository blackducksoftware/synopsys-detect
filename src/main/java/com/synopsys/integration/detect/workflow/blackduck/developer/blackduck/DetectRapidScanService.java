/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.blackduck.developer.blackduck;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.api.generated.discovery.ApiDiscovery;
import com.synopsys.integration.blackduck.api.manual.view.DeveloperScanComponentResultView;
import com.synopsys.integration.blackduck.bdio2.Bdio2FileUploadService;
import com.synopsys.integration.blackduck.bdio2.Bdio2StreamUploader;
import com.synopsys.integration.blackduck.bdio2.util.Bdio2ContentExtractor;
import com.synopsys.integration.blackduck.codelocation.upload.UploadBatch;
import com.synopsys.integration.blackduck.codelocation.upload.UploadTarget;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.detect.workflow.blackduck.developer.RapidScanResult;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.rest.HttpUrl;

public class DetectRapidScanService {
    public static final int DEFAULT_WAIT_INTERVAL_IN_SECONDS = 30;
    public static final String CONTENT_TYPE = "application/vnd.blackducksoftware.developer-scan-1-ld-2+json";

    private final Bdio2FileUploadService bdio2FileUploadService;
    private final DetectRapidScanWaiter rapidScanWaiter;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public DetectRapidScanService(Bdio2FileUploadService bdio2FileUploadService, DetectRapidScanWaiter rapidScanWaiter) {
        this.bdio2FileUploadService = bdio2FileUploadService;
        this.rapidScanWaiter = rapidScanWaiter;
    }

    public static DetectRapidScanService fromBlackDuckServicesFactory(BlackDuckServicesFactory blackDuckServicesFactory) {
        DetectRapidScanWaiter rapidScanWaiter = new DetectRapidScanWaiter(blackDuckServicesFactory.getLogger(), blackDuckServicesFactory.getBlackDuckApiClient());
        Bdio2StreamUploader bdio2Uploader = new Bdio2StreamUploader(
            blackDuckServicesFactory.getBlackDuckApiClient(),
            blackDuckServicesFactory.getApiDiscovery(),
            blackDuckServicesFactory.getLogger(),
            ApiDiscovery.DEVELOPER_SCANS_PATH,
            CONTENT_TYPE
        );
        Bdio2FileUploadService bdio2FileUploadService = new Bdio2FileUploadService(
            blackDuckServicesFactory.getBlackDuckApiClient(),
            blackDuckServicesFactory.getApiDiscovery(),
            blackDuckServicesFactory.getLogger(),
            new Bdio2ContentExtractor(),
            bdio2Uploader
        );
        return new DetectRapidScanService(bdio2FileUploadService, rapidScanWaiter);
    }

    public RapidScanResult performUpload(UploadBatch uploadBatch, long timeoutInSeconds, int waitIntervalInSeconds) throws IntegrationException, InterruptedException {
        List<DeveloperScanComponentResultView> allScanComponents = new LinkedList<>();
        List<HttpUrl> allScanUrls = new LinkedList<>();

        for (UploadTarget uploadTarget : uploadBatch.getUploadTargets()) {
            RapidScanResult scanResults = performUpload(uploadTarget, timeoutInSeconds, waitIntervalInSeconds);
            allScanComponents.addAll(scanResults.getComponentResultViews());
            allScanUrls.addAll(scanResults.getScanIds());
        }

        return new RapidScanResult(allScanUrls, allScanComponents);
    }

    public RapidScanResult performUpload(UploadTarget bdio2File, long timeoutInSeconds, int waitIntervalInSeconds) throws IntegrationException, InterruptedException {
        HttpUrl url = bdio2FileUploadService.uploadFile(bdio2File);
        logger.info("Starting Rapid Scan: {}", url);
        return RapidScanResult.forSingleScan(url, rapidScanWaiter.checkScanResult(url, bdio2File.getCodeLocationName(), timeoutInSeconds, waitIntervalInSeconds));
    }

}
