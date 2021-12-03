package com.synopsys.integration.detectable.detectables.yarn;

import java.util.ArrayList;
import java.util.List;
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
    private final List<CodeLocation> codeLocations;
    @Nullable
    private final Exception exception;

    public static YarnResult success(@Nullable String projectName, @Nullable String projectVersionName, List<CodeLocation> codeLocations) {
        return new YarnResult(projectName, projectVersionName, codeLocations, null);
    }

    public static YarnResult failure(@NotNull Exception exception) {
        return new YarnResult(null, null, new ArrayList<>(0), exception);
    }

    private YarnResult(@Nullable String projectName, @Nullable String projectVersionName, List<CodeLocation> codeLocations, @Nullable Exception exception) {
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

    @Nullable
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
