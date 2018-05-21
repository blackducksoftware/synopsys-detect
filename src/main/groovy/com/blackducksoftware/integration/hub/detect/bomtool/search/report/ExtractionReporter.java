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

import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.extraction.Extraction;
import com.blackducksoftware.integration.hub.detect.extraction.Extraction.ExtractionResult;
import com.blackducksoftware.integration.hub.detect.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.extraction.ExtractionContext;

@Component
public class ExtractionReporter {
    private final Logger logger = LoggerFactory.getLogger(ExtractionReporter.class);

    public void startedExtraction(final Strategy strategy, final ExtractionContext context) {
        logger.info(ReportConstants.SEPERATOR);
        final String strategyName = strategy.getBomToolType() + " - " + strategy.getName();
        logger.info("Starting extraction: " + strategyName);
        logger.info("Identifier: " + Integer.toString(context.hashCode()));
        logger.info("Extractor: " + strategy.getExtractorClass().getSimpleName());
        logger.info("Context: " + strategy.getExtractionContextClass().getSimpleName());
        printObject(context);
        logger.info(ReportConstants.SEPERATOR);
    }

    public void endedExtraction(final Extraction result) {
        logger.info(ReportConstants.SEPERATOR);
        logger.info("Finished extraction: " + result.result.toString());
        logger.info("Code locations found: " + result.codeLocations.size());
        if (result.result == ExtractionResult.Exception) {
            logger.info("Exception:", result.error);
        } else if (result.result == ExtractionResult.Failure) {
            logger.info(result.description);
        }
        logger.info(ReportConstants.SEPERATOR);
    }

    private void printObject(final Object guy) {
        for (final Field field : guy.getClass().getFields()) {
            final String name = field.getName();
            String value = "unknown";
            try {
                value = field.get(guy).toString();
            } catch (final Exception e) {

            }
            logger.info(name + " : " + value);
        }

    }
}
