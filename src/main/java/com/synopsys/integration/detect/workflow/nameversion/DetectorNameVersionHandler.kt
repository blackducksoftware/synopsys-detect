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
package com.synopsys.integration.detect.workflow.nameversion

import com.synopsys.integration.detector.base.DetectorType
import org.codehaus.plexus.util.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

/*
Originally, name version could be decided after all detectors had ran, there was no benefit calculating the name 'on the fly'.
With the introduction of Project Discovery (and Universal Tools) it does make sense to decide the detector project name as it happens.
The moment we have a detector discovery that we know will be our final choice for project name, we can stop further discovery.
Thus, instead of a 'Decider' that decides at the end, we have a handler that takes incoming detector discoveries.
The handler will accept until it has the 'decided' discovery and then rejects all future discoveries.
This allows discovery to run only the minimum amount of discoveries needed.
 */
open class DetectorNameVersionHandler(private val lowPriorityDetectorTypes: List<DetectorType>) {
    private val logger: Logger = LoggerFactory.getLogger(DetectorNameVersionHandler::class.java)

    val lowestDepth = ArrayList<DetectorProjectInfo>()

    open fun willAccept(metadata: DetectorProjectInfoMetadata): Boolean {
        return when {
            lowestDepth.any() -> metadata.depth <= lowestDepth.first().depth
            else -> true
        }
    }

    open fun accept(projectInfo: DetectorProjectInfo) {
        if (StringUtils.isBlank(projectInfo.nameVersion.name)) {
            return
        }

        if (lowestDepth.any()) {
            val currentDepth = lowestDepth.first().depth
            if (projectInfo.depth == currentDepth) {
                lowestDepth.add(projectInfo)
            } else if (projectInfo.depth < currentDepth) {
                lowestDepth.clear()
                lowestDepth.add(projectInfo)
            }
        } else {
            lowestDepth.add(projectInfo)
        }
    }

    open fun finalDecision(): NameVersionDecision {
        val uniqueDetectorsAtLowestDepth = filterUniqueDetectorsOnly(lowestDepth)

        return when {
            uniqueDetectorsAtLowestDepth.size == 1 -> UniqueDetectorDecision(uniqueDetectorsAtLowestDepth.first())
            uniqueDetectorsAtLowestDepth.size > 1 -> decideProjectNameVersionArbitrarily(lowestDepth)
            else -> UniqueDetectorNotFoundDecision()
        }
    }

    private fun decideProjectNameVersionArbitrarily(allPossibilities: List<DetectorProjectInfo>): NameVersionDecision {
        val normalPossibilities = allPossibilities.filter { !lowPriorityDetectorTypes.contains(it.detectorType) }

        val chosenPossibilities = when {
            normalPossibilities.isEmpty() -> allPossibilities
            else -> normalPossibilities
        }

        val chosen = chosenPossibilities.minBy { it.nameVersion.name }
        chosen?.let {
            val otherOptions = chosenPossibilities.filter { it.detectorType != chosen.detectorType }
            return ArbitraryNameVersionDecision(Optional.of(chosen.nameVersion), chosen, otherOptions.toList())
        }

        return UniqueDetectorNotFoundDecision()
    }

    //Return only project info whose detector types appear exactly once.
    internal fun filterUniqueDetectorsOnly(projectNamePossibilities: List<DetectorProjectInfo>): List<DetectorProjectInfo> {
        val grouped = projectNamePossibilities.groupBy { it.detectorType }
        val uniqueGroups = grouped.filter { it.value.size == 1 }
        return uniqueGroups.flatMap { it.value }
    }
}
