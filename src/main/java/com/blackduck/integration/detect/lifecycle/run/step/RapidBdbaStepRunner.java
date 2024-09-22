package com.blackduck.integration.detect.lifecycle.run.step;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.blackduck.integration.detect.configuration.DetectUserFriendlyException;
import com.blackduck.integration.detect.configuration.enumeration.ExitCodeType;
import com.blackduck.integration.detect.workflow.bdba.BdbaRapidScanWaitJob;
import com.blackduck.integration.detect.workflow.file.DirectoryManager;
import com.blackduck.integration.exception.IntegrationException;
import com.blackduck.integration.log.SilentIntLogger;
import com.blackduck.integration.log.Slf4jIntLogger;
import com.blackduck.integration.rest.HttpMethod;
import com.blackduck.integration.rest.HttpUrl;
import com.blackduck.integration.rest.body.BodyContent;
import com.blackduck.integration.rest.body.StringBodyContent;
import com.blackduck.integration.rest.client.IntHttpClient;
import com.blackduck.integration.rest.proxy.ProxyInfo;
import com.blackduck.integration.rest.request.Request;
import com.blackduck.integration.rest.response.Response;
import com.blackduck.integration.util.NameVersion;
import com.blackduck.integration.wait.ResilientJobConfig;
import com.blackduck.integration.wait.ResilientJobExecutor;
import com.blackduck.integration.wait.tracker.WaitIntervalTracker;
import com.blackduck.integration.wait.tracker.WaitIntervalTrackerFactory;

public class RapidBdbaStepRunner {
    
    private IntHttpClient httpClient;
    private Gson gson;
    private UUID bdbaScanId;
    private String bdbaBaseUrl;
    private int timeoutInSeconds;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    public RapidBdbaStepRunner(Gson gson, UUID bdbaScanId, int timeoutInSeconds) throws DetectUserFriendlyException {
        this.gson = gson;
        this.bdbaScanId = bdbaScanId;
        this.timeoutInSeconds = timeoutInSeconds;
        
        // Setup base client
        httpClient = new IntHttpClient(
                new SilentIntLogger(),
                gson,
                timeoutInSeconds,
                true,
                ProxyInfo.NO_PROXY_INFO
            );
        
        
        // Setup BDBA URL
        String bdbaPort = System.getenv().get("BDBA_WORKER_PORT");
        
        if (bdbaPort == null) {
            throw new DetectUserFriendlyException(
                "The port to the BDBA worker must be specified via the BDBA_WORKER_PORT envirionment variable.",
                ExitCodeType.FAILURE_CONFIGURATION
            );
        }
        
        bdbaBaseUrl = "http://localhost:" + bdbaPort;
    }

    public void submitScan(boolean isContainerScan, String filePath) throws IntegrationException, IOException {
        String containerArguments = "";
        
        if (isContainerScan) {
            containerArguments = "\"squashLayers\":true,\"scanType\":\"container\",";
        } else {
            containerArguments = "\"scanType\":\"binary\",";
        }
         
        BodyContent content = StringBodyContent.json(
                "{\"format\":\"bdio_protobuf\","
                + containerArguments
                + "\"url\":\"" + filePath + "\""
                + "}");
        Map <String, String> headers = new HashMap<>();
        Map<String, Set<String>> queryParams = new HashMap<>();
        headers.put(HttpHeaders.CONTENT_TYPE, "application/json");
        
        Request request = new Request(new HttpUrl(bdbaBaseUrl + "/scan/" + bdbaScanId), HttpMethod.POST, null, queryParams, headers, content);
        
        try (Response response = httpClient.execute(request)) {
            if (response.isStatusCodeSuccess()) {
                logger.debug("Created BDBA scan.");
            } else {
                logger.trace("Unable to create BDBA scan. Response code: " + response.getStatusCode() + " " + response.getStatusMessage());
                throw new IntegrationException("Unable to create BDBA scan. Response code: " + response.getStatusCode() + " " + response.getStatusMessage());
            }
        }   
    }

    public void pollForResults() throws InterruptedException, IntegrationException {
        WaitIntervalTracker waitIntervalTracker = WaitIntervalTrackerFactory.createProgressive(timeoutInSeconds, 60);
        ResilientJobConfig waitJobConfig = new ResilientJobConfig(new Slf4jIntLogger(logger), System.currentTimeMillis(), waitIntervalTracker);
        BdbaRapidScanWaitJob waitJob = new BdbaRapidScanWaitJob(httpClient, bdbaScanId, gson, bdbaBaseUrl);
        ResilientJobExecutor jobExecutor = new ResilientJobExecutor(waitJobConfig);
        jobExecutor.executeJob(waitJob);
    }

    public void downloadAndExtractBdio(DirectoryManager directoryManager) throws IntegrationException, IOException {
        RequestBuilder createRequestBuilder = httpClient.createRequestBuilder(HttpMethod.GET);
        
        HttpUriRequest request = createRequestBuilder
            .setUri(bdbaBaseUrl + "/scan/" + bdbaScanId)
            .build();
        
        try (Response response = httpClient.execute(request)) {
            if (response.isStatusCodeSuccess()) {
                extractBdio(directoryManager, response);
            } else {
                logger.trace("Unable to download BDIO from BDBA. Response code: " + response.getStatusCode() + " "
                        + response.getStatusMessage());
                throw new IntegrationException("Unable to download BDIO from BDBA. Response code: "
                        + response.getStatusCode() + " " + response.getStatusMessage());
            }
        }
    }

    private void extractBdio(DirectoryManager directoryManager, Response response)
            throws IntegrationException, IOException {
        logger.debug("Downloaded BDBA protobuf BDIO. Beginning extraction.");

        try (ZipInputStream zis = new ZipInputStream(response.getContent())) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                logger.debug("Extracting BDIO content: " + entry.getName());

                try (FileOutputStream fos = new FileOutputStream(
                        directoryManager.getBdioOutputDirectory().getPath() + "/" + entry.getName())) {

                    for (int byteRead = zis.read(); byteRead != -1; byteRead = zis.read()) {
                        fos.write(byteRead);
                    }

                    zis.closeEntry();
                }
            }
        }
    }
}
