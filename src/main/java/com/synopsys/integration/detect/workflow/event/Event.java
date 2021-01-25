/**
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
package com.synopsys.integration.detect.workflow.event;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.synopsys.integration.detect.lifecycle.shutdown.ExitCodeRequest;
import com.synopsys.integration.detect.tool.detector.DetectorToolResult;
import com.synopsys.integration.detect.tool.detector.executable.ExecutedExecutable;
import com.synopsys.integration.detect.workflow.codelocation.DetectCodeLocationNamesResult;
import com.synopsys.integration.detect.workflow.profiling.DetectorTimings;
import com.synopsys.integration.detect.workflow.result.DetectResult;
import com.synopsys.integration.detect.workflow.status.DetectIssue;
import com.synopsys.integration.detect.workflow.status.Status;
import com.synopsys.integration.detect.workflow.status.UnrecognizedPaths;
import com.synopsys.integration.detector.base.DetectorEvaluation;
import com.synopsys.integration.detector.base.DetectorEvaluationTree;
import com.synopsys.integration.detector.base.DetectorType;
import com.synopsys.integration.util.NameVersion;

public class Event {
    public static final EventType<DetectorEvaluationTree> SearchCompleted = new EventType<>(DetectorEvaluationTree.class);
    public static final EventType<Set<DetectorType>> ApplicableCompleted = new EventType(Set.class);
    public static final EventType<DetectorEvaluationTree> PreparationsCompleted = new EventType<>(DetectorEvaluationTree.class);
    public static final EventType<DetectorEvaluationTree> DiscoveriesCompleted = new EventType<>(DetectorEvaluationTree.class);
    public static final EventType<DetectorEvaluationTree> ExtractionsCompleted = new EventType<>(DetectorEvaluationTree.class);
    public static final EventType<DetectorToolResult> DetectorsComplete = new EventType<>(DetectorToolResult.class);
    public static final EventType<DetectorTimings> DetectorsProfiled = new EventType<>(DetectorTimings.class);
    public static final EventType<DetectorEvaluation> ApplicableStarted = new EventType<>(DetectorEvaluation.class);
    public static final EventType<DetectorEvaluation> ApplicableEnded = new EventType<>(DetectorEvaluation.class);
    public static final EventType<DetectorEvaluation> ExtractableStarted = new EventType<>(DetectorEvaluation.class);
    public static final EventType<DetectorEvaluation> ExtractableEnded = new EventType<>(DetectorEvaluation.class);
    public static final EventType<Integer> ExtractionCount = new EventType<>(Integer.class);
    public static final EventType<DetectorEvaluation> ExtractionStarted = new EventType<>(DetectorEvaluation.class);
    public static final EventType<DetectorEvaluation> ExtractionEnded = new EventType<>(DetectorEvaluation.class);
    public static final EventType<Integer> DiscoveryCount = new EventType<>(Integer.class);
    public static final EventType<DetectorEvaluation> DiscoveryStarted = new EventType<>(DetectorEvaluation.class);
    public static final EventType<DetectorEvaluation> DiscoveryEnded = new EventType<>(DetectorEvaluation.class);
    public static final EventType<DetectCodeLocationNamesResult> DetectCodeLocationNamesCalculated = new EventType<>(DetectCodeLocationNamesResult.class);
    public static final EventType<Collection<String>> CodeLocationsCompleted = new EventType(Collection.class);
    public static final EventType<ExitCodeRequest> ExitCode = new EventType<>(ExitCodeRequest.class);
    public static final EventType<Status> StatusSummary = new EventType<>(Status.class);
    public static final EventType<DetectIssue> Issue = new EventType<>(DetectIssue.class);
    public static final EventType<DetectResult> ResultProduced = new EventType<>(DetectResult.class);
    public static final EventType<File> CustomerFileOfInterest = new EventType<>(File.class);
    public static final EventType<NameVersion> ProjectNameVersionChosen = new EventType<>(NameVersion.class);
    public static final EventType<ExecutedExecutable> Executable = new EventType<>(ExecutedExecutable.class);
    public static final EventType<UnrecognizedPaths> UnrecognizedPaths = new EventType<>(UnrecognizedPaths.class);
    public static final EventType<Map> PropertyValuesCollected = new EventType<>(Map.class);
}
