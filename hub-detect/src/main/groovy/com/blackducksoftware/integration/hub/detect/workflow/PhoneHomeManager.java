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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.DetectInfo;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolGroupType;
import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.workflow.hub.OfflinePhoneHomeService;
import com.blackducksoftware.integration.hub.service.PhoneHomeService;
import com.blackducksoftware.integration.hub.service.model.PhoneHomeResponse;
import com.blackducksoftware.integration.log.IntLogger;
import com.blackducksoftware.integration.log.Slf4jIntLogger;
import com.blackducksoftware.integration.phonehome.PhoneHomeClient;
import com.blackducksoftware.integration.phonehome.PhoneHomeRequestBody;
import com.blackducksoftware.integration.phonehome.google.analytics.GoogleAnalyticsConstants;
import com.blackducksoftware.integration.util.IntEnvironmentVariables;
import com.google.gson.Gson;

public class PhoneHomeManager {
    private final Logger logger = LoggerFactory.getLogger(PhoneHomeManager.class);

    private final DetectInfo detectInfo;
    private final Gson gson;
    private final DetectConfiguration detectConfiguration;

    private PhoneHomeService phoneHomeService;
    private PhoneHomeResponse phoneHomeResponse;

    public PhoneHomeManager(final DetectInfo detectInfo, final Gson gson, final DetectConfiguration detectConfiguration) {
        this.detectInfo = detectInfo;
        this.gson = gson;
        this.detectConfiguration = detectConfiguration;
    }

    public void init(final PhoneHomeService phoneHomeService) {
        this.phoneHomeService = phoneHomeService;
    }

    public void initOffline() throws DetectUserFriendlyException {
        final IntEnvironmentVariables intEnvironmentVariables = new IntEnvironmentVariables();

        final PhoneHomeClient phoneHomeClient = new PhoneHomeClient(GoogleAnalyticsConstants.PRODUCTION_INTEGRATIONS_TRACKING_ID, logger, gson);

        final IntLogger intLogger = new Slf4jIntLogger(logger);
        this.phoneHomeService = new OfflinePhoneHomeService(intLogger, phoneHomeClient, intEnvironmentVariables);
    }

    public void startPhoneHome() {
        // hub-detect will attempt to phone home twice - once upon startup and
        // once upon getting all the bom tool metadata.
        //
        // We would prefer to always wait for all the bom tool metadata, but
        // sometimes there is not enough time to complete a phone home before
        // hub-detect exits (if the scanner is disabled, for example).
        performPhoneHome(null);
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
                final PhoneHomeRequestBody.Builder phoneHomeRequestBodyBuilder = createBuilder();
                final PhoneHomeRequestBody phoneHomeRequestBody = phoneHomeRequestBodyBuilder.build();
                if (metadata != null) {
                    for (final String metaKey : metadata.keySet()) {
                        phoneHomeRequestBodyBuilder.addToMetaData(metaKey, metadata.get(metaKey));
                    }
                }
                phoneHomeResponse = phoneHomeService.startPhoneHome(phoneHomeRequestBody);
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

    private PhoneHomeRequestBody.Builder createBuilder() {
        final PhoneHomeRequestBody.Builder phoneHomeRequestBodyBuilder = phoneHomeService.createInitialPhoneHomeRequestBodyBuilder("hub-detect", detectInfo.getDetectVersion());
        detectConfiguration.getPhoneHomeProperties().forEach((key, value) -> phoneHomeRequestBodyBuilder.addToMetaData(key, value));

        return phoneHomeRequestBodyBuilder;
    }

}
