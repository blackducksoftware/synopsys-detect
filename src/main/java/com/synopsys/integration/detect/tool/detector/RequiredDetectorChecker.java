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
package com.synopsys.integration.detect.tool.detector;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detector.base.DetectorType;

public class RequiredDetectorChecker {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static class RequiredDetectorResult {
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

    public RequiredDetectorResult checkForMissingDetectors(final List<DetectorType> requiredDetectorsString, final Set<DetectorType> applicableDetectors) {
        final Set<DetectorType> missingDetectors = requiredDetectorsString.stream()
                                                       .filter(it -> !applicableDetectors.contains(it))
                                                       .collect(Collectors.toSet());

        return new RequiredDetectorResult(missingDetectors);
    }

}
