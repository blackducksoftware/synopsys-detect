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
package com.synopsys.integration.detect.lifecycle.run.operation.blackduck;

import java.util.List;

import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.lifecycle.run.RunOptions;
import com.synopsys.integration.detect.workflow.project.DetectToolProjectInfo;
import com.synopsys.integration.detect.workflow.project.ProjectNameVersionDecider;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.util.NameVersion;

public class ProjectDecisionOperation {
    private final RunOptions runOptions;
    private final ProjectNameVersionDecider projectNameVersionDecider;

    public ProjectDecisionOperation(RunOptions runOptions, ProjectNameVersionDecider projectNameVersionDecider) {
        this.runOptions = runOptions;
        this.projectNameVersionDecider = projectNameVersionDecider;
    }

    public NameVersion execute(List<DetectToolProjectInfo> detectToolProjectInfoList) throws DetectUserFriendlyException, IntegrationException {
        return projectNameVersionDecider.decideProjectNameVersion(runOptions.getPreferredTools(), detectToolProjectInfoList);
    }
}
