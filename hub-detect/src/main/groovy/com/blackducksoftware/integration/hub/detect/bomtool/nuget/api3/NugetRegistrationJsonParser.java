package com.blackducksoftware.integration.hub.detect.bomtool.nuget.api3;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.github.zafarkhaja.semver.Version;
import com.google.gson.Gson;

/**
 * Used for parsing the JSON response from the Nuget Registration API
 */
public class NugetRegistrationJsonParser {
    final Gson gson;

    public NugetRegistrationJsonParser(final Gson gson) {
        this.gson = gson;
    }

    public List<Version> parseNugetResponse(final String jsonResponse, final String inspectorName) {
        final NugetResponse nugetResponse = gson.fromJson(jsonResponse, NugetResponse.class);

        return getVersionsFromNugetResponse(nugetResponse, inspectorName);
    }

    private List<Version> getVersionsFromNugetResponse(final NugetResponse nugetResponse, final String inspectorName) {
        final List<Version> foundVersions = new ArrayList<>();

        for (final NugetCatalogPage catalogPage : nugetResponse.getItems()) {
            for (final NugetPackage nugetPackage : catalogPage.getItems()) {
                final NugetCatalogEntry catalogEntry = nugetPackage.getCatalogEntry();
                final Optional<Version> version = getVersionFromCatalogEntry(catalogEntry, inspectorName);

                version.ifPresent(foundVersions::add);
            }
        }

        return foundVersions;
    }

    private Optional<Version> getVersionFromCatalogEntry(final NugetCatalogEntry catalogEntry, final String inspectorName) {
        Optional<Version> version = Optional.empty();
        if (isBlackDuckCatalogEntry(catalogEntry, inspectorName)) {
            final String foundVersion = catalogEntry.getPackageVersion();
            if (StringUtils.isNotBlank(foundVersion)) {
                version = Optional.of(Version.valueOf(foundVersion));
            }
        }

        return version;
    }

    private boolean isBlackDuckCatalogEntry(final NugetCatalogEntry catalogEntry, final String inspectorName) {
        final boolean nameMatches = catalogEntry.getPackageName().equals(inspectorName);
        final boolean companyMatches = catalogEntry.getAuthors().equalsIgnoreCase("Black Duck Software") || catalogEntry.getAuthors().equalsIgnoreCase("Black Duck by Synopsys");

        return nameMatches && companyMatches;
    }
}
