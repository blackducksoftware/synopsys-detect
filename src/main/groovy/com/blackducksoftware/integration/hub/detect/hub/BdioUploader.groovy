/*
 * Copyright (C) 2017 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
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

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.api.bom.BomImportRequestService
import com.blackducksoftware.integration.hub.buildtool.BuildToolConstants
import com.blackducksoftware.integration.hub.dataservice.phonehome.PhoneHomeDataService
import com.blackducksoftware.integration.hub.detect.DetectConfiguration
import com.blackducksoftware.integration.hub.global.HubServerConfig
import com.blackducksoftware.integration.hub.service.HubServicesFactory
import com.blackducksoftware.integration.log.Slf4jIntLogger
import com.blackducksoftware.integration.phonehome.PhoneHomeRequestBody

@Component
class BdioUploader {
    private final Logger logger = LoggerFactory.getLogger(BdioUploader.class)

    @Autowired
    DetectConfiguration detectConfiguration

    @Autowired
    HubManager hubManager

    void uploadBdioFiles(HubServerConfig hubServerConfig, HubServicesFactory hubServicesFactory, List<File> createdBdioFiles) {
        Slf4jIntLogger slf4jIntLogger = new Slf4jIntLogger(logger)
        BomImportRequestService bomImportRequestService = hubServicesFactory.createBomImportRequestService()
        PhoneHomeDataService phoneHomeDataService = hubServicesFactory.createPhoneHomeDataService(slf4jIntLogger)

        createdBdioFiles.each { file ->
            logger.info("uploading ${file.name} to ${detectConfiguration.getHubUrl()}")
            bomImportRequestService.importBomFile(file, BuildToolConstants.BDIO_FILE_MEDIA_TYPE)
            if (detectConfiguration.getCleanupBdioFiles()) {
                file.delete()
            }
        }

        String hubDetectVersion = detectConfiguration.getBuildInfo().getDetectVersion()
        PhoneHomeRequestBody phoneHomeRequestBody = phoneHomeDataService.buildPhoneHomeRequestBody('Hub-Detect', hubDetectVersion, hubDetectVersion)
        phoneHomeDataService.phoneHome(phoneHomeRequestBody)
    }
}
