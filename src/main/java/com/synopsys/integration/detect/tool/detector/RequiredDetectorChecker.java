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
        public RequiredDetectorResult(Set<DetectorType> missingDetectors) {
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

    public RequiredDetectorResult checkForMissingDetectors(List<DetectorType> requiredDetectorsString, Set<DetectorType> applicableDetectors) {
        Set<DetectorType> missingDetectors = requiredDetectorsString.stream()
            .filter(it -> !applicableDetectors.contains(it))
            .collect(Collectors.toSet());

        return new RequiredDetectorResult(missingDetectors);
    }

}
