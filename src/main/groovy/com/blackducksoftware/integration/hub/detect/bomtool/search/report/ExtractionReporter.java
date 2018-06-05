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

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.extraction.Extraction;
import com.blackducksoftware.integration.hub.detect.extraction.Extraction.ExtractionResult;
import com.blackducksoftware.integration.hub.detect.extraction.ExtractionContext;
import com.blackducksoftware.integration.hub.detect.strategy.Strategy;

@Component
public class ExtractionReporter {
    private final Logger logger = LoggerFactory.getLogger(ExtractionReporter.class);
    public Map<ExtractionContext, StopWatch> stopwatchMap = new HashMap<>();

    private StopWatch totalStopwatch;
    public void anyExtractionStarted() {
        totalStopwatch = new StopWatch();
        totalStopwatch.start();
    }

    public void allExtractionFinished() {
        totalStopwatch.stop();
        logger.info("All extractions finished, total time: " + totalStopwatch.getTime());
    }

    public void startedExtraction(final Strategy strategy, final ExtractionContext context) {
        logger.info(ReportConstants.SEPERATOR);
        final String strategyName = strategy.getBomToolType() + " - " + strategy.getName();
        logger.info("Starting extraction: " + strategyName);
        logger.info("Identifier: " + Integer.toString(context.hashCode()));
        logger.info("Extractor: " + strategy.getExtractorClass().getSimpleName());
        logger.info("Context: " + strategy.getExtractionContextClass().getSimpleName());
        printObject(null, context);
        logger.info(ReportConstants.SEPERATOR);

        final StopWatch sw = new StopWatch();
        sw.start();
        stopwatchMap.put(context, sw);
    }

    public void endedExtraction(final Strategy strategy, final ExtractionContext context, final Extraction result) {
        logger.info(ReportConstants.SEPERATOR);

        final StopWatch sw = stopwatchMap.get(context);
        sw.stop();

        logger.info("Finished extraction: " + result.result.toString());
        logger.info("Extraction took: " + sw.getTime());
        logger.info("Code locations found: " + result.codeLocations.size());
        if (result.result == ExtractionResult.Exception) {
            logger.info("Exception:", result.error);
        } else if (result.result == ExtractionResult.Failure) {
            logger.info(result.description);
        }
        logger.info(ReportConstants.SEPERATOR);


    }

    private void printObject(final String prefix, final Object guy) {
        for (final Field field : guy.getClass().getFields()) {
            final String name = field.getName();
            String value = "unknown";
            Object obj = null;
            try {
                obj = field.get(guy);
            } catch (final Exception e) {

            }
            if (obj == null) {
                value = "null";
            } else {
                value = obj.toString();
            }
            if (StringUtils.isBlank(prefix)) {
                logger.info(name + " : " + value);
            }else {
                logger.info(prefix + "." + name + " : " + value);
            }
            if (obj != null) {
                if (shouldRecursivelyPrintType(obj.getClass())) {
                    String nestedPrefix = name;
                    if (StringUtils.isNotBlank(prefix)) {
                        nestedPrefix = prefix + "." + nestedPrefix;
                    }
                    printObject(nestedPrefix, obj);
                }
            }
        }

    }

    public static boolean shouldRecursivelyPrintType(final Class<?> clazz)
    {
        return !NON_NESTED_TYPES.contains(clazz);
    }

    private static final Set<Class<?>> NON_NESTED_TYPES = getNonNestedTypes();

    private static Set<Class<?>> getNonNestedTypes()
    {
        final Set<Class<?>> ret = new HashSet<>();
        ret.add(File.class);
        ret.add(String.class);
        ret.add(Boolean.class);
        ret.add(Character.class);
        ret.add(Byte.class);
        ret.add(Short.class);
        ret.add(Integer.class);
        ret.add(Long.class);
        ret.add(Float.class);
        ret.add(Double.class);
        ret.add(Void.class);
        return ret;
    }
}
