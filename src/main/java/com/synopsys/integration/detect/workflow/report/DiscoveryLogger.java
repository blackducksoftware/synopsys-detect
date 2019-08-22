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
package com.synopsys.integration.detect.workflow.report;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.tool.detector.impl.DetectExtractionEnvironment;
import com.synopsys.integration.detect.workflow.report.util.ObjectPrinter;
import com.synopsys.integration.detect.workflow.report.util.ReportConstants;
import com.synopsys.integration.detect.workflow.report.writer.InfoLogReportWriter;
import com.synopsys.integration.detectable.Discovery;
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detector.base.DetectorEvaluation;

import freemarker.template.utility.StringUtil;

public class DiscoveryLogger {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private Integer discoveryCount = 0;

    public void setDiscoveryCount(final Integer count) {
        discoveryCount = count;
    }

    public void discoveryStarted(final DetectorEvaluation detectorEvaluation) {
        final DetectExtractionEnvironment detectExtractionEnvironment = (DetectExtractionEnvironment) detectorEvaluation.getExtractionEnvironment();
        final Integer i = detectExtractionEnvironment.getExtractionId().getId();
        final String progress = Integer.toString((int) Math.floor((i * 100.0f) / discoveryCount));
        logger.info(String.format("Discovery %d of %d (%s%%)", i + 1, discoveryCount, progress));
        logger.info(ReportConstants.SEPERATOR);

        logger.info("Starting discovery: " + detectorEvaluation.getDetectorRule().getDetectorType() + " - " + detectorEvaluation.getDetectorRule().getName());
        logger.info("Identifier: " + detectExtractionEnvironment.getExtractionId().toUniqueString());
        ObjectPrinter.printObjectPrivate(new InfoLogReportWriter(logger), detectorEvaluation.getDetectable());
        logger.info(ReportConstants.SEPERATOR);
    }

    public void discoveryEnded(final DetectorEvaluation detectorEvaluation) {
        logger.info(ReportConstants.SEPERATOR);
        logger.info("Finished discovery: " + detectorEvaluation.getDiscovery().getResult().toString());

        boolean projectInformationFound = StringUtils.isNotBlank(detectorEvaluation.getDiscovery().getProjectName());
        logger.info("Project information found: " + projectInformationFound);
        if (projectInformationFound) {
            logger.info("Project name: " + detectorEvaluation.getDiscovery().getProjectName());
            logger.info("Project version: " + detectorEvaluation.getDiscovery().getProjectVersion());
        }
        if (detectorEvaluation.getDiscovery().getResult() == Discovery.DiscoveryResultType.EXCEPTION) {
            logger.info("Exception: " + ExceptionUtil.oneSentenceDescription(detectorEvaluation.getDiscovery().getError()));
            logger.debug("Details: ", detectorEvaluation.getDiscovery().getError());
        } else if (detectorEvaluation.getDiscovery().getResult() == Discovery.DiscoveryResultType.FAILURE) {
            logger.info(detectorEvaluation.getDiscovery().getDescription());
        }
        logger.info(ReportConstants.SEPERATOR);
    }
}
