package com.blackduck.integration.detect.lifecycle.run.operation;

import java.io.IOException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackduck.integration.blackduck.http.BlackDuckRequestBuilder;
import com.blackduck.integration.blackduck.service.BlackDuckApiClient;
import com.blackduck.integration.blackduck.service.BlackDuckServicesFactory;
import com.blackduck.integration.blackduck.service.request.BlackDuckResponseRequest;
import com.blackduck.integration.blackduck.version.BlackDuckVersion;
import com.blackduck.integration.detect.lifecycle.run.data.BlackDuckRunData;
import com.blackduck.integration.detect.lifecycle.run.data.ScanCreationResponse;
import com.blackduck.integration.exception.IntegrationException;
import com.blackduck.integration.log.Slf4jIntLogger;
import com.blackduck.integration.rest.HttpUrl;
import com.blackduck.integration.rest.response.Response;
import com.blackduck.integration.sca.upload.client.UploaderConfig;
import com.blackduck.integration.sca.upload.client.uploaders.ScassUploader;
import com.blackduck.integration.sca.upload.client.uploaders.UploaderFactory;
import com.google.gson.Gson;

public class ScassOperationRunner {
    
    private final BlackDuckRunData blackDuckRunData;
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private static final BlackDuckVersion MIN_SCASS_SCAN_VERSION = new BlackDuckVersion(2025, 1, 0);
    
    private static final String NOTIFY_URL = "/api/scans/{}/scass-scan-processing";

    public ScassOperationRunner(BlackDuckRunData blackDuckRunData) {
        this.blackDuckRunData = blackDuckRunData;
    }
    
    public boolean areScassScansPossible() {
        Optional<BlackDuckVersion> blackDuckVersion = blackDuckRunData.getBlackDuckServerVersion();
        return blackDuckVersion.isPresent() && blackDuckVersion.get().isAtLeast(MIN_SCASS_SCAN_VERSION);
    }
    
    public ScanCreationResponse initiateScassScan() {
        // TODO refactor call
        return null;
    }
    
    public ScassUploader createScaasScanUploader() throws IntegrationException {
        UploaderConfig.Builder uploaderConfigBuilder =  UploaderConfig.createConfigFromEnvironment(
            blackDuckRunData.getBlackDuckServerConfig().getProxyInfo())
            .setBlackDuckTimeoutInSeconds(blackDuckRunData.getBlackDuckServerConfig().getTimeout())
            .setMultipartUploadTimeoutInMinutes(blackDuckRunData.getBlackDuckServerConfig().getTimeout() /  60)
            .setAlwaysTrustServerCertificate(blackDuckRunData.getBlackDuckServerConfig().isAlwaysTrustServerCertificate())
            .setBlackDuckUrl(blackDuckRunData.getBlackDuckServerConfig().getBlackDuckUrl())
            .setApiToken(blackDuckRunData.getBlackDuckServerConfig().getApiToken().get());
        
        UploaderConfig uploaderConfig = uploaderConfigBuilder.build();
        UploaderFactory uploadFactory = new UploaderFactory(uploaderConfig, new Slf4jIntLogger(logger), new Gson());
        
        return uploadFactory.createScassUploader();
    }

    public Response notifyUploadComplete(String scanId) throws IntegrationException {
        BlackDuckServicesFactory blackDuckServicesFactory = blackDuckRunData.getBlackDuckServicesFactory();
        BlackDuckApiClient blackDuckApiClient = blackDuckServicesFactory.getBlackDuckApiClient();
        
        String notfyUrl = NOTIFY_URL.replace("{}", scanId);

        HttpUrl postUrl = blackDuckRunData.getBlackDuckServerConfig().getBlackDuckUrl().appendRelativeUrl(notfyUrl);

        BlackDuckResponseRequest buildBlackDuckResponseRequest = new BlackDuckRequestBuilder()
            .addHeader("Content-Type", "application/vnd.blackducksoftware.scan-6+json")
            .post()
            .buildBlackDuckResponseRequest(postUrl);

        try (Response response = blackDuckApiClient.execute(buildBlackDuckResponseRequest)) {
            return response;
        } catch (IntegrationException e) {
            logger.trace("Could not execute JSON upload request to storage service.");
            throw new IntegrationException("Could not execute SCASS notification request.", e);
        } catch (IOException e) {
            logger.trace("I/O error occurred during SCASS notification request.");
            throw new IntegrationException("I/O error occurred during SCASS notification request.", e);
        }
    }
}
