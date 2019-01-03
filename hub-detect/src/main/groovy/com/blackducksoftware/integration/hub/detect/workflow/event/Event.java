/**
 * hub-detect
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
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
package com.blackducksoftware.integration.hub.detect.workflow.event;

import java.io.File;

import com.blackducksoftware.integration.hub.detect.detector.Detector;
import com.blackducksoftware.integration.hub.detect.lifecycle.shutdown.ExitCodeRequest;
import com.blackducksoftware.integration.hub.detect.tool.detector.DetectorToolResult;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.BdioCodeLocationResult;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.ExtractionResult;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.PreparationResult;
import com.blackducksoftware.integration.hub.detect.workflow.profiling.DetectorTimings;
import com.blackducksoftware.integration.hub.detect.workflow.search.SearchResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.DetectorEvaluation;
import com.blackducksoftware.integration.hub.detect.workflow.status.Status;

public class Event {
    public static EventType<SearchResult> SearchCompleted = new EventType(SearchResult.class);
    public static EventType<PreparationResult> PreparationsCompleted = new EventType(PreparationResult.class);
    public static EventType<ExtractionResult> ExtractionsCompleted = new EventType(ExtractionResult.class);
    public static EventType<DetectorToolResult> DetectorsComplete = new EventType(DetectorToolResult.class);
    public static EventType<DetectorTimings> DetectorsProfiled = new EventType(DetectorTimings.class);
    public static EventType<Detector> ApplicableStarted = new EventType(Detector.class);
    public static EventType<Detector> ApplicableEnded = new EventType(Detector.class);
    public static EventType<Detector> ExtractableStarted = new EventType(Detector.class);
    public static EventType<Detector> ExtractableEnded = new EventType(Detector.class);
    public static EventType<DetectorEvaluation> ExtractionStarted = new EventType(DetectorEvaluation.class);
    public static EventType<DetectorEvaluation> ExtractionEnded = new EventType(DetectorEvaluation.class);
    public static EventType<BdioCodeLocationResult> CodeLocationsCalculated = new EventType(BdioCodeLocationResult.class);
    public static EventType<ExitCodeRequest> ExitCode = new EventType(ExitCodeRequest.class);
    public static EventType<Status> StatusSummary = new EventType(Status.class);
    public static EventType<File> OutputFileOfInterest = new EventType(File.class);
    public static EventType<File> CustomerFileOfInterest = new EventType(File.class);
}
