package com.synopsys.integration.detect.workflow.blackduck.project.options;

import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import org.jetbrains.annotations.Nullable;

public class ProjectVersionLicensesFindResult {
    @Nullable
    private List<String> licenseUrls;

    public ProjectVersionLicensesFindResult(@Nullable List<String> licenseUrls) { this.licenseUrls = licenseUrls; }

    public static ProjectVersionLicensesFindResult empty() {
        return new ProjectVersionLicensesFindResult(null);
    }

    public static ProjectVersionLicensesFindResult of(@NotNull List<String> licenseUrls) {
        return new ProjectVersionLicensesFindResult(licenseUrls);
    }

    public Optional<List<String>> getLicenseUrls() {
        return Optional.ofNullable(licenseUrls);
    }
}
