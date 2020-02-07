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

import com.synopsys.integration.blackduck.codelocation.Result
import com.synopsys.integration.blackduck.codelocation.signaturescanner.ScanBatchBuilder
import com.synopsys.integration.blackduck.codelocation.signaturescanner.ScanBatchOutput
import com.synopsys.integration.blackduck.codelocation.signaturescanner.ScanBatchRunner
import com.synopsys.integration.blackduck.codelocation.signaturescanner.command.ScanCommandOutput
import com.synopsys.integration.blackduck.codelocation.signaturescanner.command.ScanTarget
import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfig
import com.synopsys.integration.detect.configuration.DetectProperties
import com.synopsys.integration.detect.exception.DetectUserFriendlyException
import com.synopsys.integration.detect.exitcode.ExitCodeType
import com.synopsys.integration.detect.lifecycle.shutdown.ExitCodeRequest
import com.synopsys.integration.detect.workflow.blackduck.ExclusionPatternCreator
import com.synopsys.integration.detect.workflow.codelocation.CodeLocationNameManager
import com.synopsys.integration.detect.workflow.event.Event
import com.synopsys.integration.detect.workflow.event.EventSystem
import com.synopsys.integration.detect.workflow.file.DirectoryManager
import com.synopsys.integration.detect.workflow.status.SignatureScanStatus
import com.synopsys.integration.detect.workflow.status.Status
import com.synopsys.integration.detect.workflow.status.StatusType
import com.synopsys.integration.detectable.detectable.file.FileFinder
import com.synopsys.integration.exception.IntegrationException
import com.synopsys.integration.util.NameVersion
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

class BlackDuckSignatureScanner(
        private val directoryManager: DirectoryManager,
        private val fileFinder: FileFinder,
        private val codeLocationNameManager: CodeLocationNameManager,
        private val signatureScannerOptions: BlackDuckSignatureScannerOptions,
        private val eventSystem: EventSystem,
        private val blackDuckServerConfig: BlackDuckServerConfig, //When OFFLINE, this should be NULL. No other changes required for offline (in this class).
        private val scanJobManager: ScanBatchRunner
) {

    private val logger = LoggerFactory.getLogger(BlackDuckSignatureScanner::class.java)

    @Throws(IntegrationException::class, IOException::class, DetectUserFriendlyException::class)
    fun performScanActions(projectNameVersion: NameVersion, installDirectory: File, dockerTarFile: File?): ScanBatchOutput {
        val signatureScanPaths = determinePathsAndExclusions(projectNameVersion, signatureScannerOptions.maxDepth, dockerTarFile)

        val scanJobBuilder = createDefaultScanBatchBuilder(projectNameVersion, installDirectory, signatureScanPaths, dockerTarFile)
        scanJobBuilder.fromBlackDuckServerConfig(blackDuckServerConfig)//when offline, we must still call this with 'null' as a workaround for library issues, so offline scanner must be created with this set to null.
        val scanJob = scanJobBuilder.build() //TODO: Catch?

        val scanCommandOutputs = ArrayList<ScanCommandOutput>()
        val scanJobOutput = scanJobManager.executeScans(scanJob)
        if (scanJobOutput.outputs != null) {
            for (scanCommandOutput in scanJobOutput.outputs) {
                scanCommandOutputs.add(scanCommandOutput)
            }
        }

        reportResults(signatureScanPaths, scanCommandOutputs)

        return scanJobOutput
    }

    //TODO: Possibly promote this to the Tool. Ideally it would return some object describing these results and the Tool translates that into detect nonsense -jp.
    private fun reportResults(signatureScanPaths: List<SignatureScanPath>, scanCommandOutputList: List<ScanCommandOutput>) {
        var anyFailed = false
        var anyExitCodeIs64 = false
        for (target in signatureScanPaths) {
            val targetOutput = scanCommandOutputList
                    .filter { output -> output.scanTarget == target.targetCanonicalPath }
                    .firstOrNull()

            val scanStatus: StatusType
            if (targetOutput == null) {
                scanStatus = StatusType.FAILURE
                logger.info(String.format("Scanning target %s was never scanned by the BlackDuck CLI.", target.targetCanonicalPath))
            } else {
                if (targetOutput.result == Result.FAILURE) {
                    scanStatus = StatusType.FAILURE

                    if (targetOutput.exception.isPresent && targetOutput.errorMessage.isPresent) {
                        logger.error(String.format("Scanning target %s failed: %s", target.targetCanonicalPath, targetOutput.errorMessage.get()))
                        logger.debug(targetOutput.errorMessage.get(), targetOutput.exception.get())
                    } else if (targetOutput.errorMessage.isPresent) {
                        logger.error(String.format("Scanning target %s failed: %s", target.targetCanonicalPath, targetOutput.errorMessage.get()))
                    } else {
                        logger.error(String.format("Scanning target %s failed for an unknown reason.", target.targetCanonicalPath))
                    }

                    if (targetOutput.scanExitCode.isPresent) {
                        anyExitCodeIs64 = anyExitCodeIs64 || targetOutput.scanExitCode.get() == 64
                    }

                } else {
                    scanStatus = StatusType.SUCCESS
                    logger.info(String.format("%s was successfully scanned by the BlackDuck CLI.", target.targetCanonicalPath))
                }
            }

            anyFailed = anyFailed || scanStatus == StatusType.FAILURE
            eventSystem.publishEvent<Status>(Event.StatusSummary, SignatureScanStatus(target.targetCanonicalPath, scanStatus))
        }

        if (anyFailed) {
            eventSystem.publishEvent(Event.ExitCode, ExitCodeRequest(ExitCodeType.FAILURE_SCAN))
        }

        if (anyExitCodeIs64) {
            logger.error("")
            logger.error("Signature scanner returned 64. The most likely cause is you are using an unsupported version of Black Duck (<5.0.0).")
            logger.error("You should update your Black Duck or downgrade your version of detect.")
            logger.error("If you are using the detect scripts, you can use DETECT_LATEST_RELEASE_VERSION.")
            logger.error("")
            eventSystem.publishEvent(Event.ExitCode, ExitCodeRequest(ExitCodeType.FAILURE_BLACKDUCK_VERSION_NOT_SUPPORTED))
        }
    }

    @Throws(IntegrationException::class, IOException::class)
    private fun determinePathsAndExclusions(projectNameVersion: NameVersion, maxDepth: Int, dockerTarFile: File?): List<SignatureScanPath> {
        val providedSignatureScanPaths = signatureScannerOptions.signatureScannerPaths
        val providedExclusionPatterns = signatureScannerOptions.exclusionPatterns
        val signatureScannerExclusionNamePatterns = signatureScannerOptions.exclusionNamePatterns

        val signatureScanPaths = ArrayList<SignatureScanPath>()
        if (null != projectNameVersion.name && null != projectNameVersion.version && providedSignatureScanPaths.isNotEmpty()) {
            for (path in providedSignatureScanPaths) {
                logger.info(String.format("Registering explicit scan path %s", path))
                val scanPath = createScanPath(path, maxDepth, signatureScannerExclusionNamePatterns, providedExclusionPatterns)
                signatureScanPaths.add(scanPath)
            }
        } else if (dockerTarFile != null) {
            val scanPath = createScanPath(dockerTarFile.canonicalFile.toPath(), maxDepth, signatureScannerExclusionNamePatterns, providedExclusionPatterns)
            signatureScanPaths.add(scanPath)
        } else {
            val sourcePath = Paths.get(directoryManager.sourceDirectory.absolutePath)
            if (providedSignatureScanPaths.isNotEmpty()) {
                logger.warn(String.format("No Project name or version found. Skipping User provided scan targets - registering the source path %s to scan", sourcePath))
            } else {
                logger.info(String.format("No scan targets provided - registering the source path %s to scan", sourcePath))
            }
            val scanPath = createScanPath(sourcePath, maxDepth, signatureScannerExclusionNamePatterns, providedExclusionPatterns)
            signatureScanPaths.add(scanPath)
        }
        return signatureScanPaths
    }

    private fun createScanPath(path: Path, maxDepth: Int, signatureScannerExclusionNamePatterns: List<String>, providedExclusionPatterns: List<String>?): SignatureScanPath {
        val target = path.toFile()
        val exclusionPatternCreator = ExclusionPatternCreator(fileFinder, target)

        val scanExclusionPatterns = exclusionPatternCreator.determineExclusionPatterns(maxDepth, signatureScannerExclusionNamePatterns)?.toMutableSet() ?: mutableSetOf()
        if (null != providedExclusionPatterns) {
            scanExclusionPatterns.addAll(providedExclusionPatterns)
        }
        val signatureScanPath = SignatureScanPath()
        signatureScanPath.targetPath = target
        signatureScanPath.exclusions.addAll(scanExclusionPatterns)
        return signatureScanPath
    }

    @Throws(DetectUserFriendlyException::class)
    protected fun createDefaultScanBatchBuilder(projectNameVersion: NameVersion, installDirectory: File, signatureScanPaths: List<SignatureScanPath>, dockerTarFile: File?): ScanBatchBuilder {
        val scanJobBuilder = ScanBatchBuilder()
        scanJobBuilder.scanMemoryInMegabytes(signatureScannerOptions.scanMemory)
        scanJobBuilder.installDirectory(installDirectory)
        scanJobBuilder.outputDirectory(directoryManager.scanOutputDirectory)

        scanJobBuilder.dryRun(signatureScannerOptions.dryRun)
        scanJobBuilder.cleanupOutput(false)

        if (signatureScannerOptions.uploadSource && signatureScannerOptions.snippetMatching == null) {
            throw DetectUserFriendlyException("You must enable snippet matching using " + DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_SNIPPET_MATCHING.key + " in order to use upload source.",
                    ExitCodeType.FAILURE_CONFIGURATION)
        }
        scanJobBuilder.uploadSource(signatureScannerOptions.snippetMatching, signatureScannerOptions.uploadSource)

        val additionalArguments = signatureScannerOptions.additionalArguments
        scanJobBuilder.additionalScanArguments(additionalArguments)

        val projectName = projectNameVersion.name
        val projectVersionName = projectNameVersion.version
        scanJobBuilder.projectAndVersionNames(projectName, projectVersionName)

        val sourcePath = directoryManager.sourceDirectory
        val prefix = signatureScannerOptions.codeLocationPrefix
        val suffix = signatureScannerOptions.codeLocationSuffix

        for (scanPath in signatureScanPaths) {
            val codeLocationName = codeLocationNameManager.createScanCodeLocationName(sourcePath, scanPath.targetPath, dockerTarFile, projectName, projectVersionName, prefix, suffix)
            scanJobBuilder.addTarget(ScanTarget.createBasicTarget(scanPath.targetCanonicalPath, scanPath.exclusions, codeLocationName))
        }

        return scanJobBuilder
    }
}