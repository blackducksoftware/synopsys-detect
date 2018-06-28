/**
 * hub-detect
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
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
package com.blackducksoftware.integration.hub.detect.bomtool.search.report;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.bomtool.BomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.ExtractionId;
import com.blackducksoftware.integration.hub.detect.manager.extraction.Extraction;
import com.blackducksoftware.integration.hub.detect.manager.extraction.Extraction.ExtractionResultType;

@Component
public class ExtractionReporter {
    private final Logger logger = LoggerFactory.getLogger(ExtractionReporter.class);

    public void startedExtraction(final BomTool bomTool, final ExtractionId extractionId) {
        logger.info(ReportConstants.SEPERATOR);
        final String bomToolName = bomTool.getBomToolGroupType() + " - " + bomTool.getName();
        logger.info("Starting extraction: " + bomToolName);
        logger.info("Identifier: " + extractionId.toUniqueString());
        // TODO: Replicate SUPER AWESOME printing from before... probably can't as nicely.
        // logger.info("Extractor: " + bomTool.getExtractorClass().getSimpleName());
        // logger.info("Context: " + bomTool.getExtractionContextClass().getSimpleName());
        // ObjectPrinter.printObject(null, context);
        logger.info(ReportConstants.SEPERATOR);
    }

    public void endedExtraction(final Extraction result) {
        logger.info(ReportConstants.SEPERATOR);
        logger.info("Finished extraction: " + result.result.toString());
        logger.info("Code locations found: " + result.codeLocations.size());
        if (result.result == ExtractionResultType.Exception) {
            logger.error("Exception:", result.error);
        } else if (result.result == ExtractionResultType.Failure) {
            logger.error(result.description);
        }
        logger.info(ReportConstants.SEPERATOR);
    }

}
