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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.service.PhoneHomeService;
import com.blackducksoftware.integration.hub.service.model.PhoneHomeResponse;
import com.blackducksoftware.integration.phonehome.PhoneHomeRequestBody;
import com.blackducksoftware.integration.phonehome.PhoneHomeRequestBodyBuilder;
import com.blackducksoftware.integration.phonehome.enums.ThirdPartyName;

@Component
public class DetectPhoneHomeManager {
    @Autowired
    private DetectInfo detectInfo;

    @Autowired
    private DetectConfiguration detectConfiguration;

    private PhoneHomeService phoneHomeService;
    private PhoneHomeResponse phoneHomeResponse;

    public void init(final PhoneHomeService phoneHomeService) {
        this.phoneHomeService = phoneHomeService;
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
        // When we begin to phone home in offline mode, we should re-address this section
        if (null != phoneHomeService) {
            final PhoneHomeRequestBodyBuilder phoneHomeRequestBodyBuilder = createBuilder();

            if (applicableBomToolTypes != null) {
                final String applicableBomToolsString = StringUtils.join(applicableBomToolTypes, ", ");
                phoneHomeRequestBodyBuilder.addToMetaDataMap("bomToolTypes", applicableBomToolsString);
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

    private PhoneHomeRequestBodyBuilder createBuilder() {
        final PhoneHomeRequestBodyBuilder phoneHomeRequestBodyBuilder = phoneHomeService.createInitialPhoneHomeRequestBodyBuilder(ThirdPartyName.DETECT, detectInfo.getDetectVersion(), detectInfo.getDetectVersion());
        detectConfiguration.getAdditionalPhoneHomePropertyNames().stream().forEach(propertyName -> {
            final String actualKey = getKeyWithoutPrefix(propertyName, DetectConfiguration.PHONE_HOME_PROPERTY_PREFIX);
            final String value = detectConfiguration.getDetectProperty(propertyName);
            phoneHomeRequestBodyBuilder.addToMetaDataMap(actualKey, value);
        });

        return phoneHomeRequestBodyBuilder;
    }

    private String getKeyWithoutPrefix(final String key, final String prefix) {
        final int prefixIndex = key.indexOf(prefix) + prefix.length();
        return key.substring(prefixIndex);
    }

}
