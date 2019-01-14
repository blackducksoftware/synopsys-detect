/**
 * hub-detect
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
package com.blackducksoftware.integration.hub.detect.tool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.DetectTool;
import com.blackducksoftware.integration.hub.detect.detector.DetectorException;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType;
import com.blackducksoftware.integration.hub.detect.lifecycle.run.RunResult;
import com.blackducksoftware.integration.hub.detect.lifecycle.shutdown.ExitCodeRequest;
import com.blackducksoftware.integration.hub.detect.workflow.event.Event;
import com.blackducksoftware.integration.hub.detect.workflow.event.EventSystem;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.DetectorResult;
import com.blackducksoftware.integration.hub.detect.workflow.status.Status;
import com.blackducksoftware.integration.hub.detect.workflow.status.StatusType;

public abstract class SimpleToolDetector {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final DetectTool toolEnum;

    public SimpleToolDetector(final DetectTool toolEnum) {
        this.toolEnum = toolEnum;
    }
    public DetectTool getToolEnum() {
        return toolEnum;
    }
    public abstract DetectorResult applicable();
    public abstract DetectorResult extractable() throws DetectorException;
    public abstract void extractAndPublishResults(final EventSystem eventSystem, final RunResult runResult);

    public void publishNotExtractableResults(final EventSystem eventSystem, final DetectorResult extractableResult) {
        logger.error(String.format("Bazel was not extractable: %s", extractableResult.toDescription()));
        eventSystem.publishEvent(Event.StatusSummary, new Status(DetectTool.BAZEL.toString(), StatusType.FAILURE));
        eventSystem.publishEvent(Event.ExitCode, new ExitCodeRequest(ExitCodeType.FAILURE_GENERAL_ERROR, extractableResult.toDescription()));
    }
}
