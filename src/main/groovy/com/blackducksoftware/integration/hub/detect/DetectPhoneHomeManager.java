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
package com.blackducksoftware.integration.hub.detect;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.hub.OfflinePhoneHomeService;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.service.PhoneHomeService;
import com.blackducksoftware.integration.hub.service.model.PhoneHomeResponse;
import com.blackducksoftware.integration.log.Slf4jIntLogger;
import com.blackducksoftware.integration.phonehome.PhoneHomeClient;
import com.blackducksoftware.integration.phonehome.PhoneHomeRequestBody;
import com.blackducksoftware.integration.phonehome.google.analytics.GoogleAnalyticsConstants;
import com.blackducksoftware.integration.util.CIEnvironmentVariables;
import com.google.gson.Gson;

@Component
public class DetectPhoneHomeManager {
    private final Logger logger = LoggerFactory.getLogger(DetectPhoneHomeManager.class);

    @Autowired
    private DetectInfo detectInfo;

    @Autowired
    private DetectConfiguration detectConfiguration;

    @Autowired
    private Gson gson;

    private PhoneHomeService phoneHomeService;
    private PhoneHomeResponse phoneHomeResponse;

    public void init(final PhoneHomeService phoneHomeService) {
        this.phoneHomeService = phoneHomeService;
    }

    public void initOffline() throws DetectUserFriendlyException {
        CIEnvironmentVariables ciEnvironmentVariables = new CIEnvironmentVariables();
        ciEnvironmentVariables.putAll(System.getenv());

        PhoneHomeClient phoneHomeClient = new PhoneHomeClient(new Slf4jIntLogger(logger), GoogleAnalyticsConstants.PRODUCTION_INTEGRATIONS_TRACKING_ID, detectConfiguration.getHubTimeout(), detectConfiguration.getHubProxyInfo(), detectConfiguration.getHubTrustCertificate(), gson);
        
        this.phoneHomeService = new OfflinePhoneHomeService(phoneHomeClient, ciEnvironmentVariables);
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

    public void startPhoneHome(final Set<BomToolType> applicableBomToolTypes) {
        performPhoneHome(applicableBomToolTypes);
    }

    private void performPhoneHome(final Set<BomToolType> applicableBomToolTypes) {
        endPhoneHome();
        if (null != phoneHomeService) {
            final PhoneHomeRequestBody.Builder phoneHomeRequestBodyBuilder = createBuilder();

            if (applicableBomToolTypes != null) {
                final String applicableBomToolsString = StringUtils.join(applicableBomToolTypes, ", ");
                phoneHomeRequestBodyBuilder.addToMetaData("bomToolTypes", applicableBomToolsString);
            }

            final PhoneHomeRequestBody phoneHomeRequestBody = phoneHomeRequestBodyBuilder.build();

            phoneHomeResponse = phoneHomeService.startPhoneHome(phoneHomeRequestBody);
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
        detectConfiguration.getAdditionalPhoneHomePropertyNames().stream().forEach(propertyName -> {
            final String actualKey = getKeyWithoutPrefix(propertyName, DetectConfiguration.PHONE_HOME_PROPERTY_PREFIX);
            final String value = detectConfiguration.getDetectProperty(propertyName);
            phoneHomeRequestBodyBuilder.addToMetaData(actualKey, value);
        });

        return phoneHomeRequestBodyBuilder;
    }

    private String getKeyWithoutPrefix(final String key, final String prefix) {
        final int prefixIndex = key.indexOf(prefix) + prefix.length();
        return key.substring(prefixIndex);
    }

}
