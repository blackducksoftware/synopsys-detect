/*
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
package com.blackducksoftware.integration.hub.detect.hub

import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.configuration.HubServerConfig
import com.blackducksoftware.integration.hub.detect.DetectConfiguration
import com.blackducksoftware.integration.hub.detect.DetectInfo
import com.blackducksoftware.integration.hub.detect.DetectPhoneHomeManager
import com.blackducksoftware.integration.hub.detect.model.BomToolType
import com.blackducksoftware.integration.hub.detect.model.DetectProject
import com.blackducksoftware.integration.hub.service.CodeLocationService
import com.blackducksoftware.integration.hub.service.PhoneHomeService
import com.blackducksoftware.integration.phonehome.PhoneHomeRequestBody
import com.blackducksoftware.integration.phonehome.PhoneHomeRequestBodyBuilder
import com.blackducksoftware.integration.phonehome.enums.ThirdPartyName

import groovy.transform.TypeChecked

@Component
@TypeChecked
class BdioUploader {
    private final Logger logger = LoggerFactory.getLogger(BdioUploader.class)

    @Autowired
    DetectInfo detectInfo

    @Autowired
    DetectConfiguration detectConfiguration

    @Autowired
    DetectPhoneHomeManager detectPhoneHomeManager

    void uploadBdioFiles(HubServerConfig hubServerConfig, CodeLocationService codeLocationService, PhoneHomeService phoneHomeService, DetectProject detectProject, List<File> createdBdioFiles) {
        createdBdioFiles.each { file ->
            logger.info("uploading ${file.name} to ${detectConfiguration.getHubUrl()}")
            codeLocationService.importBomFile(file)
            if (detectConfiguration.getCleanupBdioFiles()) {
                file.delete()
            }
        }

        String hubDetectVersion = detectInfo.detectVersion
        Set<BomToolType> applicableBomTools = detectProject.getApplicableBomTools();
        String applicableBomToolsString = StringUtils.join(applicableBomTools, ", ");

        PhoneHomeRequestBodyBuilder phoneHomeRequestBodyBuilder = phoneHomeService.createInitialPhoneHomeRequestBodyBuilder(ThirdPartyName.DETECT, hubDetectVersion, hubDetectVersion);
        phoneHomeRequestBodyBuilder.addToMetaDataMap('bomToolTypes', applicableBomToolsString);
        addAdditionalPhoneHomeMetaData(phoneHomeRequestBodyBuilder);

        PhoneHomeRequestBody phoneHomeRequestBody = phoneHomeRequestBodyBuilder.build();
        detectPhoneHomeManager.startPhoneHome(phoneHomeService, phoneHomeRequestBody);
    }

    public void addAdditionalPhoneHomeMetaData(PhoneHomeRequestBodyBuilder phoneHomeRequestBodyBuilder) {
        detectConfiguration.additionalPhoneHomePropertyNames.each { propertyName ->
            String actualKey = getKeyWithoutPrefix(propertyName, DetectConfiguration.PHONE_HOME_PROPERTY_PREFIX)
            String value = detectConfiguration.getDetectProperty(propertyName);
            phoneHomeRequestBodyBuilder.addToMetaDataMap(actualKey, value);
        }
    }
    private String getKeyWithoutPrefix(String key, String prefix) {
        return key[prefix.length()..-1]
    }
}
