/**
 * synopsys-detect
 *
 * Copyright (c) 2019 Synopsys, Inc.
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
package com.synopsys.integration.detect.kotlin.nameversion

import com.synopsys.integration.detect.configuration.DetectProperty
import java.util.Optional

import org.slf4j.Logger

import com.synopsys.integration.util.NameVersion
import com.synopsys.integration.detector.base.DetectorType

abstract class NameVersionDecision(val chosenNameVersion: Optional<NameVersion> = Optional.empty()) {
    abstract fun printDescription(logger: Logger)
}

class ArbitraryNameVersionDecision(chosenNameVersion: Optional<NameVersion>, private val chosenDetector: DetectorProjectInfo, private val otherDetectors: List<DetectorProjectInfo>) : NameVersionDecision(chosenNameVersion) {
    override fun printDescription(logger: Logger) {
        logger.info("The following project names were found: ")
        logger.info("\t${chosenDetector.detectorType}: ${chosenDetector.nameVersion.name}, ${chosenDetector.nameVersion.version}")
        for (projectNamePossibility in otherDetectors) {
            logger.info("\t${projectNamePossibility.detectorType}: ${projectNamePossibility.nameVersion.name}, ${projectNamePossibility.nameVersion.version}")
        }
        logger.info("Chose to use ${chosenDetector.detectorType} at depth ${chosenDetector.depth} for project name and version. Override with " + DetectProperty.DETECT_PROJECT_DETECTOR.propertyKey + ".")
    }
}

class PreferredDetectorDecision(private val chosenDetectorProjectInfo: DetectorProjectInfo) : NameVersionDecision(Optional.of(chosenDetectorProjectInfo.nameVersion)) {
    override fun printDescription(logger: Logger) {
        logger.debug("Using preferred bom tool project info from ${chosenDetectorProjectInfo.detectorType} found at depth ${chosenDetectorProjectInfo.depth} as project info.")
    }
}

class PreferredDetectorNotFoundDecision(private val detectorType: DetectorType) : NameVersionDecision() {
    override fun printDescription(logger: Logger) {
        logger.debug("A detector of type $detectorType was not found. Project info could not be found in a detector.")
    }
}

class TooManyPreferredDetectorTypesFoundDecision(private val detectorType: DetectorType) : NameVersionDecision() {
    override fun printDescription(logger: Logger) {
        logger.debug("More than one preferred detector of type $detectorType was found. Project info could not be found in a detector.")
    }
}

class UniqueDetectorDecision(private val chosenDetectorProjectInfo: DetectorProjectInfo) : NameVersionDecision(Optional.of(chosenDetectorProjectInfo.nameVersion)) {
    override fun printDescription(logger: Logger) {
        logger.debug("Exactly one unique detector was found. Using ${chosenDetectorProjectInfo.detectorType} found at depth ${chosenDetectorProjectInfo.depth} as project info.")
    }
}

class UniqueDetectorNotFoundDecision : NameVersionDecision() {
    override fun printDescription(logger: Logger) {
        logger.debug("No unique detector was found. Project info could not be found in a detector.")
    }
}
