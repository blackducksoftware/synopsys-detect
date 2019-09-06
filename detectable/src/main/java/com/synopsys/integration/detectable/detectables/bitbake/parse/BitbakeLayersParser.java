/**
 * detectable
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
package com.synopsys.integration.detectable.detectables.bitbake.parse;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.detectables.bitbake.model.BitbakeLayer;
import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.log.Slf4jIntLogger;

public class BitbakeLayersParser {
    private final IntLogger logger = new Slf4jIntLogger(LoggerFactory.getLogger(this.getClass()));

    public Map<String, Integer> parseLayerPriorityMap(final String output) {
        final Map<String, Integer> layerPriorityMap = new HashMap<>();

        boolean started = false;
        for (final String line : output.split(System.lineSeparator())) {
            if (StringUtils.isBlank(line)) {
                continue;
            }

            if (!started && line.startsWith("=====================================")) {
                started = true;
                continue;
            } else if (!started) {
                continue;
            }

            parseLine(line).ifPresent(bitbakeLayer -> layerPriorityMap.put(bitbakeLayer.getLayerName(), bitbakeLayer.getPriority()));
        }

        return layerPriorityMap;
    }

    private Optional<BitbakeLayer> parseLine(final String line) {
        final String trimmedLine = line.trim();

        final int indexOfFirstSpace = trimmedLine.indexOf(" ");
        if (indexOfFirstSpace == -1) {
            logger.debug(String.format("Failed to find bitbake layer name field from line '%s'", line));
            return Optional.empty();
        }
        final String layerName = trimmedLine.substring(0, indexOfFirstSpace);

        final int indexOfLastSpace = trimmedLine.lastIndexOf(" ");
        if (indexOfLastSpace + 1 > trimmedLine.length()) {
            logger.debug(String.format("Failed to find bitbake layer priority field from line '%s'.", line));
            return Optional.empty();
        }
        final String layerPriorityString = trimmedLine.substring(indexOfLastSpace + 1);

        try {
            final int layerPriority = Integer.parseInt(layerPriorityString);
            return Optional.of(new BitbakeLayer(layerName, layerPriority));
        } catch (final NumberFormatException e) {
            logger.debug(String.format("Failed to parse bitbake layer priority from line '%s'.", line), e);
            return Optional.empty();
        }
    }
}
