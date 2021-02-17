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
package com.synopsys.integration.detect.lifecycle.run.operation.input;

import com.synopsys.integration.blackduck.service.model.ProjectVersionWrapper;
import com.synopsys.integration.detect.workflow.bdio.BdioResult;
import com.synopsys.integration.detect.workflow.blackduck.codelocation.CodeLocationResults;
import com.synopsys.integration.util.NameVersion;

public class FullScanPostProcessingInput {
    private final NameVersion projectNameVersion;
    private final BdioResult bdioResult;
    private final CodeLocationResults codeLocationResults;
    private final ProjectVersionWrapper projectVersionWrapper;

    public FullScanPostProcessingInput(NameVersion projectNameVersion, BdioResult bdioResult, CodeLocationResults codeLocationResults, ProjectVersionWrapper projectVersionWrapper) {
        this.projectNameVersion = projectNameVersion;
        this.bdioResult = bdioResult;
        this.codeLocationResults = codeLocationResults;
        this.projectVersionWrapper = projectVersionWrapper;

    }

    public NameVersion getProjectNameVersion() {
        return projectNameVersion;
    }

    public BdioResult getBdioResult() {
        return bdioResult;
    }

    public CodeLocationResults getCodeLocationResults() {
        return codeLocationResults;
    }

    public ProjectVersionWrapper getProjectVersionWrapper() {
        return projectVersionWrapper;
    }

}
