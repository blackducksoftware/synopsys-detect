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
import java.io.InputStream;
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

import org.apache.commons.io.FileUtils;
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

    public Response submitScan(boolean squashLayers) throws IntegrationException, IOException {
        // TODO have to be told somehow where file is, using existing property in arguments?
        BodyContent content = StringBodyContent.json(
                "{\"format\":\"bdio_protobuf\", \"squashLayers\": "
                + squashLayers
                + ", \"url\":\"file:///foo/TEW-636APB-1002-Firmware.bin\"}");
        Map <String, String> headers = new HashMap<>();
        Map<String, Set<String>> queryParams = new HashMap<>();
        headers.put(HttpHeaders.CONTENT_TYPE, "application/json");
        
        // TODO also have to be told where the worker is. Will get somehow when detect is invoked?
        Request request = new Request(new HttpUrl("http://localhost:9001/scan/" + bdbaScanId), HttpMethod.POST, null, queryParams, headers, content);
        
        try (Response response = httpClient.execute(request)) {
            if (response.isStatusCodeSuccess()) {
                logger.debug("Created BDBA scan.");
                return response;
            } else {
                logger.trace("Unable to create BDBA scan. Response code: " + response.getStatusCode() + " " + response.getStatusMessage());
                throw new IntegrationException("Unable to create BDBA scan. Response code: " + response.getStatusCode() + " " + response.getStatusMessage());
            }
        }   
    }

    public BdbaStatusScanView pollForResults() throws InterruptedException, IntegrationException {
        WaitIntervalTracker waitIntervalTracker = WaitIntervalTrackerFactory.createProgressive(DEFAULT_TIMEOUT, 60);
        ResilientJobConfig waitJobConfig = new ResilientJobConfig(new Slf4jIntLogger(logger), System.currentTimeMillis(), waitIntervalTracker);
        BinaryRapidScanWaitJob waitJob = new BinaryRapidScanWaitJob(httpClient, bdbaScanId, gson);
        ResilientJobExecutor jobExecutor = new ResilientJobExecutor(waitJobConfig);
        return jobExecutor.executeJob(waitJob);
    }

    public void downloadAndExtractBdio(DirectoryManager directoryManager, NameVersion projectVersion) throws IntegrationException, IOException {
        RequestBuilder createRequestBuilder = httpClient.createRequestBuilder(HttpMethod.GET);
        
        // TODO get or pass location of BDBA worker
        HttpUriRequest request = createRequestBuilder
            .setUri("http://localhost:9001/scan/" + bdbaScanId)
            .build();
        
        try (Response response = httpClient.execute(request)) {
            if (response.isStatusCodeSuccess()) {
                logger.debug("Downloaded BDBA protobuf BDIO. Beginning extraction.");

                ZipInputStream zis = new ZipInputStream(response.getContent());

                ZipEntry entry;
                while ((entry = zis.getNextEntry()) != null) {
                    logger.debug("Extracting BDIO content: " + entry.getName());

                    FileOutputStream fos = new FileOutputStream(directoryManager.getBdioOutputDirectory().getPath() + "/" + entry.getName());
                    
                    for (int byteRead = zis.read(); byteRead != -1; byteRead = zis.read()) {
                        fos.write(byteRead);
                    }
                    
                    zis.closeEntry();
                    fos.close();
                }

                zis.close();
            } else {
                logger.trace("Unable to download BDIO from BDBA. Response code: " + response.getStatusCode() + " "
                        + response.getStatusMessage());
                throw new IntegrationException("Unable to download BDIO from BDBA. Response code: "
                        + response.getStatusCode() + " " + response.getStatusMessage());
            }
        }
    }
}
