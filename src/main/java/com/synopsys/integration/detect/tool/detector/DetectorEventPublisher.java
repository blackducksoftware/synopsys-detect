/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.tool.detector;

import java.io.File;
import java.util.Set;

import com.synopsys.integration.detect.workflow.status.UnrecognizedPaths;
import com.synopsys.integration.detector.base.DetectorEvaluationTree;
import com.synopsys.integration.detector.base.DetectorType;

public interface DetectorEventPublisher {
    void publishApplicableCompleted(Set<DetectorType> applicableDetectors);

    void publishCustomerFileOfInterest(File file);

    void publishDetectorsComplete(DetectorToolResult detectorToolResult);

    void publishDiscoveryCount(Integer discoveryCount);

    void publishDiscoveriesCompleted(DetectorEvaluationTree detectorEvaluationTree);

    void publishExtractionCount(Integer extractionCount);

    void publishExtractionsCompleted(DetectorEvaluationTree detectorEvaluationTree);

    void publishPreparationsCompleted(DetectorEvaluationTree detectorEvaluationTree);

    void publishSearchCompleted(DetectorEvaluationTree detectorEvaluationTree);

    void publishUnrecognizedPaths(UnrecognizedPaths unrecognizedPaths);
}
