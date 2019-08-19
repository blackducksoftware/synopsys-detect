package com.synopsys.integration.detector.evaluation;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detector.base.DetectorEvaluation;

public interface DiscoveryFilter {
    boolean shouldDiscover(DetectorEvaluation detectorEvaluation);
}
