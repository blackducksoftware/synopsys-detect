/**
 * hub-detect
 *
 * Copyright (C) 2019 Black Duck Software, Inc.
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
package com.blackducksoftware.integration.hub.detect.workflow.phonehome;

import java.util.Map;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.DetectInfo;
import com.blackducksoftware.integration.hub.detect.workflow.event.EventSystem;
import com.google.gson.Gson;
import com.synopsys.integration.log.Slf4jIntLogger;
import com.synopsys.integration.phonehome.PhoneHomeClient;
import com.synopsys.integration.phonehome.PhoneHomeResponse;
import com.synopsys.integration.phonehome.PhoneHomeService;
import com.synopsys.integration.phonehome.google.analytics.GoogleAnalyticsConstants;
import com.synopsys.integration.util.IntEnvironmentVariables;

public class OfflinePhoneHomeManager extends PhoneHomeManager {
    private final Logger logger = LoggerFactory.getLogger(OfflinePhoneHomeManager.class);

    private PhoneHomeClient phoneHomeClient;
    private PhoneHomeService phoneHomeService;

    public OfflinePhoneHomeManager(Map<String, String> additionalMetaData, final DetectInfo detectInfo, final Gson gson, EventSystem eventSystem) {
        super(additionalMetaData, detectInfo, gson, eventSystem);

        this.phoneHomeService = new PhoneHomeService(new Slf4jIntLogger(logger), Executors.newSingleThreadExecutor());
        this.phoneHomeClient = new PhoneHomeClient(GoogleAnalyticsConstants.PRODUCTION_INTEGRATIONS_TRACKING_ID, new Slf4jIntLogger(logger), gson);
    }

    @Override
    public PhoneHomeResponse phoneHome(final Map<String, String> metadata) {
        OfflineBlackDuckPhoneHomeCallable offlineCallable = new OfflineBlackDuckPhoneHomeCallable(new Slf4jIntLogger(logger), phoneHomeClient, "hub-detect", detectInfo.getDetectVersion(), new IntEnvironmentVariables());

        metadata.forEach((key, value) -> offlineCallable.addMetaData(key, value));
        additionalMetaData.forEach((key, value) -> metadata.put(key, value));

        return phoneHomeService.startPhoneHome(offlineCallable);
    }

}
