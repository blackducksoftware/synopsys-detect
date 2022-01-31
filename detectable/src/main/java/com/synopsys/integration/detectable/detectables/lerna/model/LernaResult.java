package com.synopsys.integration.detectable.detectables.lerna.model;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;

// TODO: Have this class produce success an failure objects for a clearer API.
public class LernaResult {
    @Nullable
    private final String projectName;
    @Nullable
    private final String projectVersionName;
    private final List<CodeLocation> codeLocations;
    @Nullable
    private final Exception exception;

    public static LernaResult success(@Nullable String projectName, @Nullable String projectVersionName, List<CodeLocation> codeLocations) {
        return new LernaResult(projectName, projectVersionName, codeLocations, null);
    }

    public static LernaResult failure(@NotNull Exception exception) {
        return new LernaResult(null, null, Collections.emptyList(), exception);
    }

    private LernaResult(@Nullable String projectName, @Nullable String projectVersionName, List<CodeLocation> codeLocations, @Nullable Exception exception) {
        this.projectName = projectName;
        this.projectVersionName = projectVersionName;
        this.codeLocations = codeLocations;
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

    public List<CodeLocation> getCodeLocations() {
        return codeLocations;
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
