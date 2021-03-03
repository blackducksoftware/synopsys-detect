/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
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
