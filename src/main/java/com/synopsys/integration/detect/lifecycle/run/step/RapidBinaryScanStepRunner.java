/*
 * Copyright (C) 2023 Synopsys Inc.
 * http://www.synopsys.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Synopsys ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Synopsys.
 */
package com.synopsys.integration.detect.lifecycle.run.step;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.synopsys.integration.blackduck.api.generated.view.ProjectView;
import com.synopsys.integration.blackduck.http.BlackDuckRequestBuilder;
import com.synopsys.integration.blackduck.service.BlackDuckApiClient;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.blackduck.service.request.BlackDuckResponseRequest;
import com.synopsys.integration.detect.lifecycle.run.data.BlackDuckRunData;
import com.synopsys.integration.detect.workflow.bdba.BdbaStatusScanView;
import com.synopsys.integration.detect.workflow.bdba.BinaryRapidScanWaitJob;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.log.SilentIntLogger;
import com.synopsys.integration.log.Slf4jIntLogger;
import com.synopsys.integration.rest.HttpMethod;
import com.synopsys.integration.rest.HttpUrl;
import com.synopsys.integration.rest.body.BodyContent;
import com.synopsys.integration.rest.body.EntityBodyContent;
import com.synopsys.integration.rest.body.StringBodyContent;
import com.synopsys.integration.rest.client.IntHttpClient;
import com.synopsys.integration.rest.proxy.ProxyInfo;
import com.synopsys.integration.rest.request.Request;
import com.synopsys.integration.rest.response.Response;
import com.synopsys.integration.util.IntegrationEscapeUtil;
import com.synopsys.integration.util.NameVersion;
import com.synopsys.integration.wait.ResilientJobConfig;
import com.synopsys.integration.wait.ResilientJobExecutor;
import com.synopsys.integration.wait.tracker.WaitIntervalTracker;
import com.synopsys.integration.wait.tracker.WaitIntervalTrackerFactory;

public class RapidBinaryScanStepRunner {
    
    private IntHttpClient httpClient;
    private Gson gson;
    private UUID bdbaScanId;
    private UUID bdScanId;
    private static final int DEFAULT_TIMEOUT = 300;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    public RapidBinaryScanStepRunner(Gson gson, UUID bdbaScanId) {
        this.gson = gson;
        this.bdbaScanId = bdbaScanId;
        
        httpClient = new IntHttpClient(
                new SilentIntLogger(),
                gson,
                Math.toIntExact(DEFAULT_TIMEOUT),
                true,
                ProxyInfo.NO_PROXY_INFO
            );
    }

    public Response submitScan() throws IntegrationException {
        // TODO have to be told somehow where file is, using existing property in arguments?
        BodyContent content = 
                //StringBodyContent.json("{\"format\":\"protobuf\", \"url\":\"file:///foo/TEW-636APB-1002-Firmware.bin\"}");
                //StringBodyContent.json("{\"url\":\"file:///foo/TEW-636APB-1002-Firmware.bin\"}");
                StringBodyContent.json("{\"creator\":\"foo\", \"url\":\"file:///foo/TEW-636APB-1002-Firmware.bin\"}");
        Map <String, String> headers = new HashMap<>();
        Map<String, Set<String>> queryParams = new HashMap<>();
        headers.put(HttpHeaders.CONTENT_TYPE, "application/json");
        
        Request request = new Request(new HttpUrl("http://localhost:9001/scan/" + bdbaScanId), HttpMethod.POST, null, queryParams, headers, content);
        return httpClient.execute(request);   
    }

    public BdbaStatusScanView pollForResults() throws InterruptedException, IntegrationException {
        WaitIntervalTracker waitIntervalTracker = WaitIntervalTrackerFactory.createProgressive(DEFAULT_TIMEOUT, 60);
        ResilientJobConfig waitJobConfig = new ResilientJobConfig(new Slf4jIntLogger(logger), System.currentTimeMillis(), waitIntervalTracker);
        BinaryRapidScanWaitJob waitJob = new BinaryRapidScanWaitJob(httpClient, bdbaScanId, gson);
        ResilientJobExecutor jobExecutor = new ResilientJobExecutor(waitJobConfig);
        return jobExecutor.executeJob(waitJob);
    }

    public void downloadAndExtractBdio(DirectoryManager directoryManager, NameVersion projectVersion) throws IntegrationException {
        RequestBuilder createRequestBuilder = httpClient.createRequestBuilder(HttpMethod.GET);
        HttpUriRequest request = createRequestBuilder
            .setUri("http://localhost:9001/scan/" + bdbaScanId)
            .build();
        Response response = httpClient.execute(request);
        
        ZipInputStream zis = new ZipInputStream(response.getContent());
        
        ZipEntry entry;
        try {
            while((entry = zis.getNextEntry()) != null) {
                String s = String.format("Entry: %s len %d added %TD",
                        entry.getName(), entry.getSize(),
                        new Date(entry.getTime()));
                System.out.println(s);
                
                // TODO safe on windows?
                FileOutputStream fos = new FileOutputStream(directoryManager.getBdioOutputDirectory().getPath() + "/" + entry.getName());
                for (int byteRead = zis.read(); byteRead != -1; byteRead = zis.read()) {
                    fos.write(byteRead);
                }
                zis.closeEntry();
                fos.close();
            }
            
            zis.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // TODO move to operationRunnner since this isn't really BDBA? It's like initiateRapidBinaryScan
    public void submitBdioChunk(BlackDuckRunData blackDuckRunData) throws IntegrationException {
//        BlackDuckServicesFactory blackDuckServicesFactory = blackDuckRunData.getBlackDuckServicesFactory();
//        BlackDuckApiClient blackDuckApiClient = blackDuckServicesFactory.getBlackDuckApiClient();
//        
//        HttpUrl scanUrl = blackDuckApiClient.post(new HttpUrl("https://localhost/api/developer-scans/" + bdScanId.toString()), null);
////        ProjectView projectView = blackDuckApiClient.getResponse(scanUrl, ProjectView.class);
        
        BlackDuckServicesFactory blackDuckServicesFactory = blackDuckRunData.getBlackDuckServicesFactory();
        BlackDuckApiClient blackDuckApiClient = blackDuckServicesFactory.getBlackDuckApiClient();

        HttpUrl postUrl = new HttpUrl(blackDuckRunData.getBlackDuckServerConfig().getBlackDuckUrl().toString() + "/api/developer-scans");

        BlackDuckResponseRequest buildBlackDuckResponseRequest = new BlackDuckRequestBuilder()
                .addHeader("Content-type", "application/vnd.blackducksoftware.scan-evidence-1+protob")
                .put() // putString or other similar if necessary
                .buildBlackDuckResponseRequest(postUrl);

        Response response = blackDuckApiClient.execute(buildBlackDuckResponseRequest);

    }

    public void setBdScanId(UUID bdScanId) {
        this.bdScanId = bdScanId;
    }
}
