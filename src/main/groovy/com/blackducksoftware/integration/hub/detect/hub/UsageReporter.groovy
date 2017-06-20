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


import org.joda.time.DateTime
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.dataservice.phonehome.PhoneHomeDataService
import com.blackducksoftware.integration.hub.detect.DetectConfiguration
import com.blackducksoftware.integration.hub.phonehome.IntegrationInfo
import com.blackducksoftware.integration.hub.service.HubServicesFactory
import com.blackducksoftware.integration.log.Slf4jIntLogger

@Component
class UsageReporter {
    private final Logger logger = LoggerFactory.getLogger(this.getClass())

    @Autowired
    HubManager hubManager

    @Autowired
    DetectConfiguration detectConfiguration

    void phoneHome() {
        def outputDirectory = new File(detectConfiguration.outputDirectory, 'detect_util')
        outputDirectory.mkdir()
        def timestampFile = new File(outputDirectory, 'usageTimestamp.txt')

        if(timestampFile.exists()) {
            String timeText = timestampFile.getText()
            Long previousTime = Long.parseLong(timeText)
            if(previousTime == DateTime.now().withTimeAtStartOfDay().getMillis()) {
                logger.debug('Already executed UsageReporter today. Not attempting again')
                return
            }
        }

        try {
            def version = '0.0.5-SNAPSHOT'
            IntegrationInfo integrationInfo = new IntegrationInfo('Hub-Detect', version, version)

            HubServicesFactory hubServicesFactory = hubManager.createHubServicesFactory(logger)
            PhoneHomeDataService phoneHomeDataService = hubServicesFactory.createPhoneHomeDataService(new Slf4jIntLogger(logger))
            phoneHomeDataService.phoneHome(hubManager.createHubServerConfig(logger), integrationInfo)
        } catch (Exception e) {
            logger.error("Your Hub configuration is not valid: ${e.message}")
            if(logger.isTraceEnabled()) {
                e.printStackTrace()
            }
            return
        }


        Long dateTime = DateTime.now().withTimeAtStartOfDay().getMillis()
        timestampFile.delete()
        timestampFile << dateTime.toString()

        logger.debug('Phonehome complete')
    }
}
