/**
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
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
