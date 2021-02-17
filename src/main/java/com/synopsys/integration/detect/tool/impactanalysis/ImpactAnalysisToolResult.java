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
package com.synopsys.integration.detect.tool.impactanalysis;

import java.nio.file.Path;
import java.util.Collections;
import java.util.Set;

import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationData;
import com.synopsys.integration.blackduck.service.model.NotificationTaskRange;
import com.synopsys.integration.detect.tool.impactanalysis.service.ImpactAnalysisBatchOutput;

public class ImpactAnalysisToolResult {
    @Nullable
    private final CodeLocationCreationData<ImpactAnalysisBatchOutput> codeLocationCreationData;
    @Nullable
    private final Path impactAnalysisPath;

    public static ImpactAnalysisToolResult SUCCESS(CodeLocationCreationData<ImpactAnalysisBatchOutput> codeLocationCreationData, Path impactAnalysisPath) {
        return new ImpactAnalysisToolResult(codeLocationCreationData, impactAnalysisPath);
    }

    public static ImpactAnalysisToolResult SUCCESS(Path impactAnalysisPath) {
        return new ImpactAnalysisToolResult(null, impactAnalysisPath);
    }

    public static ImpactAnalysisToolResult FAILURE() {
        return new ImpactAnalysisToolResult(null, null);
    }

    private ImpactAnalysisToolResult(@Nullable CodeLocationCreationData<ImpactAnalysisBatchOutput> codeLocationCreationData, @Nullable Path impactAnalysisPath) {
        this.codeLocationCreationData = codeLocationCreationData;
        this.impactAnalysisPath = impactAnalysisPath;
    }

    public boolean isSuccessful() {
        if (null != codeLocationCreationData) {
            return !codeLocationCreationData.getOutput().getSuccessfulCodeLocationNames().isEmpty();
        } else {
            return null != impactAnalysisPath;
        }
    }

    public NotificationTaskRange getNotificationTaskRange() {
        if (null == codeLocationCreationData) {
            return null;
        }
        return codeLocationCreationData.getNotificationTaskRange();
    }

    public Set<String> getCodeLocationNames() {
        if (null == codeLocationCreationData) {
            return Collections.emptySet();
        }
        return codeLocationCreationData.getOutput().getSuccessfulCodeLocationNames();
    }

    @Nullable
    public CodeLocationCreationData<ImpactAnalysisBatchOutput> getCodeLocationCreationData() {
        return codeLocationCreationData;
    }

    @Nullable
    public Path getImpactAnalysisPath() {
        return impactAnalysisPath;
    }
}
