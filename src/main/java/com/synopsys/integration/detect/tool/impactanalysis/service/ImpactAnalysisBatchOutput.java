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
package com.synopsys.integration.detect.tool.impactanalysis.service;

import java.util.List;

import org.slf4j.Logger;

import com.synopsys.integration.blackduck.codelocation.CodeLocationBatchOutput;
import com.synopsys.integration.blackduck.exception.BlackDuckIntegrationException;

public class ImpactAnalysisBatchOutput extends CodeLocationBatchOutput<ImpactAnalysisOutput> {
    public ImpactAnalysisBatchOutput(List<ImpactAnalysisOutput> outputs) {
        super(outputs);
    }

    public void throwExceptionForError(Logger logger) throws BlackDuckIntegrationException {
        for (ImpactAnalysisOutput impactAnalysisOutput : this) {
            if (impactAnalysisOutput.getStatusCode() == 404) {
                logger.error("Impact analysis upload failed with 404. Your version of Black Duck may not support Vulnerability Impact Analysis.");
            } else if (impactAnalysisOutput.getStatusCode() < 200 || impactAnalysisOutput.getStatusCode() >= 300) {
                logger.error(String.format("Unknown status code: %d", impactAnalysisOutput.getStatusCode()));
                throw new BlackDuckIntegrationException(String.format("Unknown status code when uploading impact analysis: %d, %s", impactAnalysisOutput.getStatusCode(), impactAnalysisOutput.getStatusMessage()));
            }
        }
    }

}
