/**
 * synopsys-detect
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
package com.synopsys.integration.detect.workflow.event;

import java.io.File;
import java.util.Set;

import com.synopsys.integration.detect.lifecycle.shutdown.ExitCodeRequest;
import com.synopsys.integration.detect.tool.detector.DetectorToolResult;
import com.synopsys.integration.detect.workflow.codelocation.BdioCodeLocationResult;
import com.synopsys.integration.detect.workflow.profiling.DetectorTimings;
import com.synopsys.integration.detect.workflow.status.DetectResult;
import com.synopsys.integration.detect.workflow.status.Status;
import com.synopsys.integration.detector.base.DetectorEvaluation;
import com.synopsys.integration.detector.base.DetectorEvaluationTree;
import com.synopsys.integration.detector.base.DetectorType;

public class Event {
    public static EventType<DetectorEvaluationTree> SearchCompleted = new EventType(DetectorEvaluationTree.class);
    public static EventType<Set<DetectorType>> ApplicableCompleted = new EventType(Set.class);
    public static EventType<DetectorEvaluationTree> PreparationsCompleted = new EventType(DetectorEvaluationTree.class);
    public static EventType<DetectorEvaluationTree> ExtractionsCompleted = new EventType(DetectorEvaluationTree.class);
    public static EventType<DetectorToolResult> DetectorsComplete = new EventType(DetectorToolResult.class);
    public static EventType<DetectorTimings> DetectorsProfiled = new EventType(DetectorTimings.class);
    public static EventType<DetectorEvaluation> ApplicableStarted = new EventType(DetectorEvaluation.class);
    public static EventType<DetectorEvaluation> ApplicableEnded = new EventType(DetectorEvaluation.class);
    public static EventType<DetectorEvaluation> ExtractableStarted = new EventType(DetectorEvaluation.class);
    public static EventType<DetectorEvaluation> ExtractableEnded = new EventType(DetectorEvaluation.class);
    public static EventType<Integer> ExtractionCount = new EventType(Integer.class);
    public static EventType<DetectorEvaluation> ExtractionStarted = new EventType(DetectorEvaluation.class);
    public static EventType<DetectorEvaluation> ExtractionEnded = new EventType(DetectorEvaluation.class);
    public static EventType<BdioCodeLocationResult> CodeLocationsCalculated = new EventType(BdioCodeLocationResult.class);
    public static EventType<ExitCodeRequest> ExitCode = new EventType(ExitCodeRequest.class);
    public static EventType<Status> StatusSummary = new EventType(Status.class);
    public static EventType<DetectResult> ResultProduced = new EventType(DetectResult.class);
    public static EventType<File> OutputFileOfInterest = new EventType(File.class);
    public static EventType<File> CustomerFileOfInterest = new EventType(File.class);
}
