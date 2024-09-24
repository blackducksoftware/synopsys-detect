package com.blackduck.integration.detect.workflow.blackduck.developer.blackduck;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackduck.integration.blackduck.api.generated.discovery.ApiDiscovery;
import com.blackduck.integration.blackduck.bdio2.util.Bdio2ContentExtractor;
import com.blackduck.integration.blackduck.codelocation.upload.UploadBatch;
import com.blackduck.integration.blackduck.codelocation.upload.UploadTarget;
import com.blackduck.integration.blackduck.service.BlackDuckServicesFactory;
import com.blackduck.integration.detect.workflow.blackduck.developer.RapidScanOptions;
import com.blackduck.integration.detect.workflow.file.DirectoryManager;
import com.blackduck.integration.exception.IntegrationException;
import com.blackduck.integration.rest.HttpUrl;

public class DetectRapidScanService {
    public static final String CONTENT_TYPE = "application/vnd.blackducksoftware.developer-scan-1-ld-2+json";

    private final RapidScanUploadService bdio2FileUploadService;
    private final DirectoryManager directoryManager;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public DetectRapidScanService(RapidScanUploadService bdio2FileUploadService, DirectoryManager directoryManager) {
        this.bdio2FileUploadService = bdio2FileUploadService;
        this.directoryManager = directoryManager;
    }

    public static DetectRapidScanService fromBlackDuckServicesFactory(DirectoryManager directoryManager, BlackDuckServicesFactory blackDuckServicesFactory) {
        RapidScanConfigBdio2StreamUploader bdio2Uploader = new RapidScanConfigBdio2StreamUploader(
            blackDuckServicesFactory.getBlackDuckApiClient(),
            blackDuckServicesFactory.getApiDiscovery(),
            blackDuckServicesFactory.getLogger(),
            ApiDiscovery.DEVELOPER_SCANS_PATH,
            CONTENT_TYPE
        );
        RapidScanUploadService bdio2FileUploadService = new RapidScanUploadService(
            blackDuckServicesFactory.getBlackDuckApiClient(),
            blackDuckServicesFactory.getApiDiscovery(),
            blackDuckServicesFactory.getLogger(),
            new Bdio2ContentExtractor(),
            bdio2Uploader
        );
        return new DetectRapidScanService(bdio2FileUploadService, directoryManager);
    }

    public List<HttpUrl> performUpload(UploadBatch uploadBatch, RapidScanOptions rapidScanOptions, @Nullable File rapidScanConfig) throws IntegrationException, IOException, InterruptedException {
        List<HttpUrl> allScanUrls = new LinkedList<>();

        for (UploadTarget uploadTarget : uploadBatch.getUploadTargets()) {
            HttpUrl url = bdio2FileUploadService.uploadFile(directoryManager.getRapidOutputDirectory(), uploadTarget, rapidScanOptions, rapidScanConfig);
            String scanModeString = rapidScanOptions.getScanMode().displayName();
            logger.info("Uploaded " + scanModeString + " Scan: {}", url);
            allScanUrls.add(url);
        }

        return allScanUrls;
    }
}
