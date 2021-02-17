/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
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
package com.synopsys.integration.detect.workflow.report;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.tool.detector.DetectExtractionEnvironment;
import com.synopsys.integration.detect.workflow.report.util.ObjectPrinter;
import com.synopsys.integration.detect.workflow.report.util.ReportConstants;
import com.synopsys.integration.detect.workflow.report.writer.DebugLogReportWriter;
import com.synopsys.integration.detectable.Discovery;
import com.synopsys.integration.detector.base.DetectorEvaluation;

public class DiscoveryLogger {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private Integer discoveryCount = 0;

    public void setDiscoveryCount(Integer count) {
        discoveryCount = count;
    }

    public void discoveryStarted(DetectorEvaluation detectorEvaluation) {
        DetectExtractionEnvironment detectExtractionEnvironment = (DetectExtractionEnvironment) detectorEvaluation.getExtractionEnvironment();
        Integer i = detectExtractionEnvironment.getExtractionId().getId();
        String progress = Integer.toString((int) Math.floor((i * 100.0f) / discoveryCount));
        logger.debug(String.format("Discovery %d of %d (%s%%)", i + 1, discoveryCount, progress));
        logger.debug(ReportConstants.SEPERATOR);

        logger.debug("Starting discovery: " + detectorEvaluation.getDetectorType() + " - " + detectorEvaluation.getDetectorRule().getName());
        logger.debug("Identifier: " + detectExtractionEnvironment.getExtractionId().toUniqueString());
        ObjectPrinter.printObjectPrivate(new DebugLogReportWriter(logger), detectorEvaluation.getDetectable());
        logger.debug(ReportConstants.SEPERATOR);
    }

    public void discoveryEnded(DetectorEvaluation detectorEvaluation) {
        logger.debug(ReportConstants.SEPERATOR);
        logger.debug("Finished discovery: " + detectorEvaluation.getDiscovery().getResult().toString());

        boolean projectInformationFound = StringUtils.isNotBlank(detectorEvaluation.getDiscovery().getProjectName());
        logger.debug("Project information found: " + projectInformationFound);
        if (projectInformationFound) {
            logger.debug("Project name found: " + detectorEvaluation.getDiscovery().getProjectName());
            logger.debug("Project version found: " + detectorEvaluation.getDiscovery().getProjectVersion());
        }
        if (detectorEvaluation.getDiscovery().getResult() == Discovery.DiscoveryResultType.EXCEPTION) {
            logger.debug("Exception: " + ExceptionUtil.oneSentenceDescription(detectorEvaluation.getDiscovery().getError()));
            logger.debug("Details: ", detectorEvaluation.getDiscovery().getError());
        } else if (detectorEvaluation.getDiscovery().getResult() == Discovery.DiscoveryResultType.FAILURE) {
            logger.debug(detectorEvaluation.getDiscovery().getDescription());
        }
        logger.debug(ReportConstants.SEPERATOR);
    }
}
