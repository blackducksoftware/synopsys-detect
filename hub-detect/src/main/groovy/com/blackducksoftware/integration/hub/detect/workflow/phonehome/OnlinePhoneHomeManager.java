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
package com.blackducksoftware.integration.hub.detect.workflow.phonehome;

import java.net.URL;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.DetectInfo;
import com.blackducksoftware.integration.hub.detect.hub.HubServiceManager;
import com.blackducksoftware.integration.hub.detect.workflow.event.EventSystem;
import com.google.gson.Gson;
import com.synopsys.integration.blackduck.service.model.BlackDuckPhoneHomeCallable;
import com.synopsys.integration.phonehome.PhoneHomeResponse;
import com.synopsys.integration.phonehome.PhoneHomeService;

public class OnlinePhoneHomeManager extends PhoneHomeManager {
    private final Logger logger = LoggerFactory.getLogger(OnlinePhoneHomeManager.class);

    private URL hubUrl;

    private PhoneHomeService phoneHomeService;
    private HubServiceManager hubServiceManager;

    public OnlinePhoneHomeManager(Map<String, String> additionalMetaData, final DetectInfo detectInfo, final Gson gson, EventSystem eventSystem, final HubServiceManager hubServiceManager) {
        super(additionalMetaData, detectInfo, gson, eventSystem);
        this.hubServiceManager = hubServiceManager;

        hubUrl = hubServiceManager.getHubServicesFactory().getRestConnection().getBaseUrl();
        phoneHomeService = hubServiceManager.createPhoneHomeService();
    }

    @Override
    public PhoneHomeResponse phoneHome(final Map<String, String> metadata) {
        final BlackDuckPhoneHomeCallable onlineCallable = (BlackDuckPhoneHomeCallable) hubServiceManager.getHubServicesFactory().createBlackDuckPhoneHomeCallable(hubUrl, "hub-detect", detectInfo.getDetectVersion());
        metadata.forEach((key, value) -> onlineCallable.addMetaData(key, value));
        additionalMetaData.forEach((key, value) -> metadata.put(key, value));
        return phoneHomeService.startPhoneHome(onlineCallable);
    }
}
