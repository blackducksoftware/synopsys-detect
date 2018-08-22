/**
 * hub-detect
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.blackducksoftware.integration.hub.detect.workflow;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.DetectInfo;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolGroupType;
import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.google.gson.Gson;
import com.synopsys.integration.blackduck.service.HubRegistrationService;
import com.synopsys.integration.blackduck.service.HubService;
import com.synopsys.integration.blackduck.service.model.BlackDuckPhoneHomeCallable;
import com.synopsys.integration.log.Slf4jIntLogger;
import com.synopsys.integration.phonehome.PhoneHomeCallable;
import com.synopsys.integration.phonehome.PhoneHomeClient;
import com.synopsys.integration.phonehome.PhoneHomeResponse;
import com.synopsys.integration.phonehome.PhoneHomeService;
import com.synopsys.integration.phonehome.google.analytics.GoogleAnalyticsConstants;
import com.synopsys.integration.util.IntEnvironmentVariables;

public class PhoneHomeManager {
    private final Logger logger = LoggerFactory.getLogger(PhoneHomeManager.class);

    private final DetectInfo detectInfo;
    private URL hubUrl;

    private PhoneHomeClient phoneHomeClient;
    private PhoneHomeService phoneHomeService;
    private PhoneHomeResponse phoneHomeResponse;
    private HubService hubService;
    private HubRegistrationService hubRegistrationService;
    private boolean isBlackDuckOffline;
    private IntEnvironmentVariables environmentVariables;
    private final DetectConfiguration detectConfiguration;
    private final Gson gson;

    public PhoneHomeManager(final DetectInfo detectInfo, final DetectConfiguration detectConfiguration, final Gson gson) {
        this.detectInfo = detectInfo;
        this.detectConfiguration = detectConfiguration;
        this.gson = gson;
    }

    public void initOffline() {

        this.phoneHomeService = new PhoneHomeService(new Slf4jIntLogger(logger), Executors.newSingleThreadExecutor());
        this.phoneHomeClient = new PhoneHomeClient(GoogleAnalyticsConstants.PRODUCTION_INTEGRATIONS_TRACKING_ID, new Slf4jIntLogger(logger), gson);

        this.isBlackDuckOffline = true;
        this.environmentVariables = new IntEnvironmentVariables();
    }

    public void init(final PhoneHomeService phoneHomeService, final PhoneHomeClient phoneHomeClient, final IntEnvironmentVariables environmentVariables, final HubService hubService,
            final HubRegistrationService hubRegistrationService, final URL hubUrl) {
        this.phoneHomeService = phoneHomeService;
        this.isBlackDuckOffline = false;
        this.environmentVariables = environmentVariables;
        this.hubService = hubService;
        this.hubRegistrationService = hubRegistrationService;
        this.hubUrl = hubUrl;
        this.phoneHomeClient = phoneHomeClient;
    }

    public void startPhoneHome() {
        // hub-detect will attempt to phone home twice - once upon startup and
        // once upon getting all the bom tool metadata.
        //
        // We would prefer to always wait for all the bom tool metadata, but
        // sometimes there is not enough time to complete a phone home before
        // hub-detect exits (if the scanner is disabled, for example).
        performPhoneHome(new HashMap<>());
    }

    public void startPhoneHome(final Set<BomToolGroupType> applicableBomToolTypes) {
        final Map<String, String> metadata = new HashMap<>();
        if (applicableBomToolTypes != null) {
            final String applicableBomToolsString = applicableBomToolTypes.stream()
                    .map(BomToolGroupType::toString)
                    .collect(Collectors.joining(","));
            metadata.put("bomToolTypes", applicableBomToolsString);
        }
        performPhoneHome(metadata);
    }

    public void startPhoneHome(final Map<BomToolGroupType, Long> applicableBomToolTimes) {
        final Map<String, String> metadata = new HashMap<>();
        if (applicableBomToolTimes != null) {
            final String applicableBomToolsString = applicableBomToolTimes.keySet().stream()
                    .map(it -> String.format("%s:%s", it.toString(), applicableBomToolTimes.get(it)))
                    .collect(Collectors.joining(","));
            metadata.put("bomToolTypes", applicableBomToolsString);
        }
        performPhoneHome(metadata);
    }

    private void performPhoneHome(final Map<String, String> metadata) {
        endPhoneHome();
        if (null != phoneHomeService) {
            try {
                final PhoneHomeCallable callable;
                final Set<Entry<String, String>> additionalMetadata = detectConfiguration.getBlackduckProperties().entrySet();
                additionalMetadata.forEach(it -> metadata.put(it.getKey(), it.getValue()));
                if (isBlackDuckOffline) {
                    callable = createOfflineCallable(metadata);
                } else {
                    callable = createOnlineCallable(metadata);
                }

                phoneHomeResponse = phoneHomeService.startPhoneHome(callable);
            } catch (final IllegalStateException e) {
                logger.debug(e.getMessage(), e);
            }
        }
    }

    public void endPhoneHome() {
        if (phoneHomeResponse != null) {
            phoneHomeResponse.endPhoneHome();
        }
    }

    public PhoneHomeResponse getPhoneHomeResponse() {
        return phoneHomeResponse;
    }

    // TODO: Don't supply a product url to offine phone home!
    private PhoneHomeCallable createOfflineCallable(final Map<String, String> metadata) {
        OfflineBlackDuckPhoneHomeCallable offlineCallable;
        offlineCallable = new OfflineBlackDuckPhoneHomeCallable(new Slf4jIntLogger(logger), phoneHomeClient, "hub-detect", detectInfo.getDetectVersion(), environmentVariables);
        metadata.entrySet().forEach(it -> offlineCallable.addMetaData(it.getKey(), it.getValue()));

        return offlineCallable;

    }

    private PhoneHomeCallable createOnlineCallable(final Map<String, String> metadata) {
        final BlackDuckPhoneHomeCallable onlineCallable = new BlackDuckPhoneHomeCallable(new Slf4jIntLogger(logger), phoneHomeClient, hubUrl, "hub-detect", detectInfo.getDetectVersion(), environmentVariables, hubService,
                hubRegistrationService);
        metadata.entrySet().forEach(it -> onlineCallable.addMetaData(it.getKey(), it.getValue()));

        return onlineCallable;
    }

}
