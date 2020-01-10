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
package com.synopsys.integration.detect.workflow.nameversion

import com.synopsys.integration.detector.base.DetectorType
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/*
Originally, name version could be decided after all detectors had ran, there was no benefit calculating the name 'on the fly'.
With the introduction of Project Discovery (and Universal Tools) it does make sense to decide the detector project name as it happens.
The moment we have a detector discovery that we know will be our final choice for project name, we can stop further discovery.
Thus, instead of a 'Decider' that decides at the end, we have a handler that takes incoming detector discoveries.
The handler will accept until it has the 'decided' discovery and then rejects all future discoveries.
This allows discovery to run only the minimum amount of discoveries needed.
 */
class PreferredDetectorNameVersionHandler(private val preferredDetectorType: DetectorType) : DetectorNameVersionHandler(emptyList()) {
    private val logger: Logger = LoggerFactory.getLogger(PreferredDetectorNameVersionHandler::class.java)

    override fun willAccept(metadata: DetectorProjectInfoMetadata): Boolean {
        return when (metadata.detectorType) {
            preferredDetectorType -> super.willAccept(metadata)
            else -> false
        }
    }

    override fun accept(projectInfo: DetectorProjectInfo) {
        if (projectInfo.detectorType == preferredDetectorType) {
            super.accept(projectInfo)
        }
    }

    override fun finalDecision(): NameVersionDecision {
        val uniqueDetectorsAtLowestDepth = filterUniqueDetectorsOnly(lowestDepth)

        return when {
            uniqueDetectorsAtLowestDepth.isEmpty() -> PreferredDetectorNotFoundDecision(preferredDetectorType)
            uniqueDetectorsAtLowestDepth.size == 1 -> PreferredDetectorDecision(uniqueDetectorsAtLowestDepth.first())
            else -> TooManyPreferredDetectorTypesFoundDecision(preferredDetectorType)
        }
    }
}
