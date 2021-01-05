/**
 * detectable
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
package com.synopsys.integration.detectable.detectables.yarn;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;

// TODO: Have this class produce success an failure objects for a clearer API.
public class YarnResult {
    @Nullable
    private final String projectName;
    @Nullable
    private final String projectVersionName;
    private final CodeLocation codeLocation;
    @Nullable
    private final Exception exception;

    public static YarnResult success(@Nullable String projectName, @Nullable String projectVersionName, CodeLocation codeLocation) {
        return new YarnResult(projectName, projectVersionName, codeLocation, null);
    }

    public static YarnResult failure(@NotNull Exception exception) {
        return new YarnResult(null, null, null, exception);
    }

    private YarnResult(@Nullable String projectName, @Nullable String projectVersionName, @Nullable CodeLocation codeLocation, @Nullable Exception exception) {
        this.projectName = projectName;
        this.projectVersionName = projectVersionName;
        this.codeLocation = codeLocation;
        this.exception = exception;
    }

    @Nullable
    public String getProjectName() {
        return projectName;
    }

    @Nullable
    public String getProjectVersionName() {
        return projectVersionName;
    }

    @Nullable
    public CodeLocation getCodeLocation() {
        return codeLocation;
    }

    public Optional<Exception> getException() {
        return Optional.ofNullable(exception);
    }

    public boolean isFailure() {
        return !isSuccess();
    }

    public boolean isSuccess() {
        return !getException().isPresent();
    }
}
