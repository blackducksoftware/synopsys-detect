/*
 * detector
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detector.evaluation;

import com.synopsys.integration.detector.base.DetectorEvaluation;

public interface DiscoveryFilter {
    boolean shouldDiscover(DetectorEvaluation detectorEvaluation);
}
