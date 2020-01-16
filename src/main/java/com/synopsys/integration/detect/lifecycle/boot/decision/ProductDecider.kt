/**
 * synopsys-detect
 *
 * Copyright (c) 2020 Synopsys, Inc.
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
package com.synopsys.integration.detect.lifecycle.boot.decision

import com.synopsys.integration.detect.DetectTool
import com.synopsys.integration.detect.configuration.DetectConfigurationFactory
import com.synopsys.integration.detect.util.filter.DetectToolFilter
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import java.io.File

class ProductDecider(private val detectConfigurationFactory: DetectConfigurationFactory) {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    fun decide(userHome: File, detectToolFilter: DetectToolFilter): ProductDecision {
        return ProductDecision(determineBlackDuck(), determinePolaris(userHome, detectToolFilter))
    }

    private fun determinePolaris(userHome: File, detectToolFilter: DetectToolFilter): PolarisDecision {
        if (!detectToolFilter.shouldInclude(DetectTool.POLARIS)) {
            logger.debug("Polaris will NOT run because it is excluded.")
            return PolarisDecision.skip()
        }
        val polarisServerConfigBuilder = detectConfigurationFactory.createPolarisServerConfigBuilder(userHome)
        val builderStatus = polarisServerConfigBuilder.validateAndGetBuilderStatus()
        val polarisCanRun = builderStatus.isValid

        if (!polarisCanRun) {
            val polarisUrl = polarisServerConfigBuilder.url
            if (StringUtils.isBlank(polarisUrl)) {
                logger.debug("Polaris will NOT run: The Polaris url must be provided.")
            } else {
                logger.debug("Polaris will NOT run: " + builderStatus.fullErrorMessage)
            }
            return PolarisDecision.skip()
        } else {
            logger.debug("Polaris will run: An access token and url were found.")
            return PolarisDecision.runOnline(polarisServerConfigBuilder.build())
        }
    }

    private fun determineBlackDuck(): BlackDuckDecision {
        val blackDuckConnectionDetails = detectConfigurationFactory.createBlackDuckConnectionDetails()
        val offline = blackDuckConnectionDetails.offline
        val blackDuckUrl = blackDuckConnectionDetails.blackduckUrl

        val blackDuckSignatureScannerOptions = detectConfigurationFactory.createBlackDuckSignatureScannerOptions()
        val signatureScannerHostUrl = blackDuckSignatureScannerOptions.userProvidedScannerInstallUrl
        val signatureScannerOfflineLocalPath = blackDuckSignatureScannerOptions.offlineLocalScannerInstallPath
        if (offline) {
            logger.debug("Black Duck will run: Black Duck offline mode was set to true.")
            return BlackDuckDecision.runOffline()
        } else if (StringUtils.isNotBlank(signatureScannerHostUrl)) {
            logger.info("A Black Duck signature scanner url was provided, which requires Black Duck offline mode.")
            return BlackDuckDecision.runOffline()
        } else if (StringUtils.isNotBlank(signatureScannerOfflineLocalPath)) {
            logger.info("A local Black Duck signature scanner path was provided, which requires Black Duck offline mode.")
            return BlackDuckDecision.runOffline()
        } else if (StringUtils.isNotBlank(blackDuckUrl)) {
            logger.debug("Black Duck will run: A Black Duck url was found.")
            return BlackDuckDecision.runOnline()
        } else {
            logger.debug("Black Duck will NOT run: The Black Duck url must be provided or offline mode must be set to true.")
            return BlackDuckDecision.skip()
        }
    }
}
