package com.synopsys.integration.detect.workflow.blackduck.project.options;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ProjectVersionLicenseFindResult {
    @Nullable
    private final String licenseUrl;

    public ProjectVersionLicenseFindResult(@Nullable String licenseUrl) {this.licenseUrl = licenseUrl;}

    public static ProjectVersionLicenseFindResult empty() {
        return new ProjectVersionLicenseFindResult(null);
    }

    public static ProjectVersionLicenseFindResult of(@NotNull String licenseUrl) {
        return new ProjectVersionLicenseFindResult(licenseUrl);
    }

    public Optional<String> getLicenseUrl() {
        return Optional.ofNullable(licenseUrl);
    }
}
