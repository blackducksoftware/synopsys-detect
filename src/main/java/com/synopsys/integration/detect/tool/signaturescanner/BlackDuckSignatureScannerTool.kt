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
package com.synopsys.integration.detect.tool.signaturescanner

import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationData
import com.synopsys.integration.blackduck.codelocation.signaturescanner.ScanBatchRunner
import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfig
import com.synopsys.integration.detect.configuration.ConnectionFactory
import com.synopsys.integration.detect.configuration.DetectConfigurationFactory
import com.synopsys.integration.detect.exception.DetectUserFriendlyException
import com.synopsys.integration.detect.lifecycle.DetectContext
import com.synopsys.integration.detect.lifecycle.run.data.BlackDuckRunData
import com.synopsys.integration.detect.workflow.file.DirectoryManager
import com.synopsys.integration.exception.IntegrationException
import com.synopsys.integration.log.SilentIntLogger
import com.synopsys.integration.util.IntEnvironmentVariables
import com.synopsys.integration.util.NameVersion
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.nio.file.Path
import java.util.*
import java.util.concurrent.Executors

class BlackDuckSignatureScannerTool(private val signatureScannerOptions: BlackDuckSignatureScannerOptions, private val detectContext: DetectContext) {
    private val logger = LoggerFactory.getLogger(BlackDuckSignatureScannerTool::class.java)

    @Throws(DetectUserFriendlyException::class)
    fun runScanTool(blackDuckRunData: BlackDuckRunData, projectNameVersion: NameVersion, dockerTar: Optional<File>): SignatureScannerToolResult {
        val detectConfigurationFactory = detectContext.getBean(DetectConfigurationFactory::class.java)
        val connectionFactory = detectContext.getBean(ConnectionFactory::class.java)
        val directoryManager = detectContext.getBean(DirectoryManager::class.java)

        var blackDuckServerConfig = Optional.empty<BlackDuckServerConfig>()
        if (blackDuckRunData.isOnline && blackDuckRunData.blackDuckServerConfig.isPresent) {
            blackDuckServerConfig = blackDuckRunData.blackDuckServerConfig
        }

        var localScannerInstallPath: Path? = null
        if (signatureScannerOptions.offlineLocalScannerInstallPath != null) {
            localScannerInstallPath = signatureScannerOptions.offlineLocalScannerInstallPath
        } else if (signatureScannerOptions.onlineLocalScannerInstallPath != null) {
            localScannerInstallPath = signatureScannerOptions.onlineLocalScannerInstallPath
        }
        localScannerInstallPath?.let { logger.debug("Determined offline local scanner path: $localScannerInstallPath") }

        val blackDuckSignatureScannerOptions = detectConfigurationFactory.createBlackDuckSignatureScannerOptions()
        val executorService = Executors.newFixedThreadPool(blackDuckSignatureScannerOptions.parallelProcessors)
        val intEnvironmentVariables = IntEnvironmentVariables()

        val scanBatchRunnerFactory = ScanBatchRunnerFactory(intEnvironmentVariables, executorService)
        val scanBatchRunner: ScanBatchRunner
        var installDirectory = directoryManager.permanentDirectory
        if (blackDuckServerConfig.isPresent && signatureScannerOptions.userProvidedScannerInstallUrl != null && localScannerInstallPath != null) {
            logger.debug("Signature scanner will use the Black Duck server to download/update the scanner - this is the most likely situation.")
            scanBatchRunner = scanBatchRunnerFactory.withInstall(blackDuckServerConfig.get())
        } else {
            if (signatureScannerOptions.userProvidedScannerInstallUrl != null) {
                logger.debug("Signature scanner will use the provided url to download/update the scanner.")
                val restConnection = connectionFactory.createConnection(signatureScannerOptions.userProvidedScannerInstallUrl, SilentIntLogger()) //TODO: Should this be silent?
                scanBatchRunner = scanBatchRunnerFactory.withUserProvidedUrl(signatureScannerOptions.userProvidedScannerInstallUrl, restConnection)
            } else {
                logger.debug("Signature scanner either given an existing path for the scanner or is offline - either way, we won't attempt to manage the install.")
                if (localScannerInstallPath != null) {
                    logger.debug("Using provided path: $localScannerInstallPath")
                    installDirectory = localScannerInstallPath.toFile()
                } else {
                    logger.debug("Using default scanner path.")
                }
                scanBatchRunner = scanBatchRunnerFactory.withoutInstall(installDirectory)
            }
        }
        logger.debug("Determined install directory: " + installDirectory.absolutePath)

        try {
            //When offline, server config is null, otherwise scanner is created the same way online/offline.
            val blackDuckSignatureScanner = detectContext.getBean(BlackDuckSignatureScanner::class.java, signatureScannerOptions, scanBatchRunner, blackDuckServerConfig.orElse(null))
            if (blackDuckServerConfig.isPresent) {
                logger.debug("Signature scan is online.")
                //Since we are online, we need to calculate the notification task range to wait for code locations.
                val codeLocationCreationService = blackDuckRunData.blackDuckServicesFactory.get().createCodeLocationCreationService()
                val notificationTaskRange = codeLocationCreationService.calculateCodeLocationRange()
                val scanBatchOutput = blackDuckSignatureScanner.performScanActions(projectNameVersion, installDirectory, dockerTar.orElse(null))
                val codeLocationCreationData = CodeLocationCreationData(notificationTaskRange, scanBatchOutput)
                return SignatureScannerToolResult.createOnlineResult(codeLocationCreationData)
            } else {
                logger.debug("Signature scan is offline.")
                //Since we are offline, we can just perform the scan actions.
                val scanBatchOutput = blackDuckSignatureScanner.performScanActions(projectNameVersion, installDirectory, dockerTar.orElse(null))
                return SignatureScannerToolResult.createOfflineResult(scanBatchOutput)
            }
        } catch (e: IOException) {
            logger.error(String.format("Signature scan failed: %s", e.message))
            logger.debug("Signature scan error", e)
            return SignatureScannerToolResult.createFailureResult()
        } catch (e: IntegrationException) {
            logger.error(String.format("Signature scan failed: %s", e.message))
            logger.debug("Signature scan error", e)
            return SignatureScannerToolResult.createFailureResult()
        } finally {
            executorService.shutdownNow()
        }
    }

}