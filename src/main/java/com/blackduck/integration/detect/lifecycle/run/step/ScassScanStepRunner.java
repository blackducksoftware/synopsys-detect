package com.blackduck.integration.detect.lifecycle.run.step;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackduck.integration.blackduck.http.BlackDuckRequestBuilder;
import com.blackduck.integration.blackduck.service.BlackDuckApiClient;
import com.blackduck.integration.blackduck.service.BlackDuckServicesFactory;
import com.blackduck.integration.blackduck.service.request.BlackDuckResponseRequest;
import com.blackduck.integration.detect.lifecycle.run.data.BlackDuckRunData;
import com.blackduck.integration.detect.lifecycle.run.step.utility.MultipartUploaderHelper;
import com.blackduck.integration.exception.IntegrationException;
import com.blackduck.integration.rest.HttpUrl;
import com.blackduck.integration.rest.response.Response;
import com.blackduck.integration.sca.upload.client.uploaders.ScassUploader;
import com.blackduck.integration.sca.upload.client.uploaders.UploaderFactory;
import com.blackduck.integration.sca.upload.rest.status.DefaultUploadStatus;
import com.blackduck.integration.sca.upload.rest.status.UploadStatus;

public class ScassScanStepRunner {
    
    private final BlackDuckRunData blackDuckRunData;
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private static final String NOTIFY_URL = "/api/scans/{}/scass-scan-processing";

    public ScassScanStepRunner(BlackDuckRunData blackDuckRunData) {
        this.blackDuckRunData = blackDuckRunData;
    }
    
    public void runScassScan(Optional<File> scanFile, String scanId, String uploadUrl) throws IntegrationException {        
        ScassUploader scaasScanUploader = createScaasScanUploader();
        
        // TODO hardcode headers for now until we know if we need them
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-type", "application/octet-stream");
        
        UploadStatus status = scaasScanUploader.upload(uploadUrl, headers, scanFile.get().toPath());
        
        if (status.isError()) {
            MultipartUploaderHelper.handleUploadError((DefaultUploadStatus)status);
        }
        
        // call /scans/{scanId}/scass-scan-processing to notify BlackDuck the file is uploaded
        notifyUploadComplete(scanId);
    }
    
    private ScassUploader createScaasScanUploader() throws IntegrationException {
        UploaderFactory uploadFactory = MultipartUploaderHelper.getUploaderFactory(blackDuckRunData);
        
        return uploadFactory.createScassUploader();
    }

    private Response notifyUploadComplete(String scanId) throws IntegrationException {
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
