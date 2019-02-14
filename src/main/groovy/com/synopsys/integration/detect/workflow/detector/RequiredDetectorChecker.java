/**
 * synopsys-detect
 *
 * Copyright (C) 2019 Black Duck Software, Inc.
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
package com.synopsys.integration.detect.workflow.detector;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.detector.DetectorType;

public class RequiredDetectorChecker {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public class RequiredDetectorResult {
        public RequiredDetectorResult(final Set<DetectorType> missingDetectors) {
            this.missingDetectors = missingDetectors;
        }

        public boolean wereDetectorsMissing() {
            return missingDetectors.size() > 0;
        }

        public Set<DetectorType> getMissingDetectors() {
            return missingDetectors;
        }

        private final Set<DetectorType> missingDetectors;
    }

    public RequiredDetectorResult checkForMissingDetectors(final String requiredDetectorsString, final Set<DetectorType> applicableDetectors) {
        final Set<DetectorType> required = parseRequiredDetectors(requiredDetectorsString);

        final Set<DetectorType> missingDetectors = required.stream()
                                                       .filter(it -> !applicableDetectors.contains(it))
                                                       .collect(Collectors.toSet());

        return new RequiredDetectorResult(missingDetectors);
    }

    private Set<DetectorType> parseRequiredDetectors(final String rawRequiredTypeString) {
        final Set<DetectorType> required = new HashSet<>();
        final String[] rawRequiredTypes = rawRequiredTypeString.split(",");
        for (final String rawType : rawRequiredTypes) {
            if (StringUtils.isBlank(rawType))
                continue;

            try {
                final DetectorType type = DetectorType.valueOf(rawType.toUpperCase());
                required.add(type);
            } catch (IllegalArgumentException e) {
                logger.error("Unable to parse detector type: " + rawType);
            }
        }
        return required;
    }
}
