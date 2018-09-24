package com.blackducksoftware.integration.hub.detect.bomtool.nuget.api3;

import java.util.Optional;

import com.google.gson.Gson;

public class NugetIndexJsonParser {
    final Gson gson;

    public NugetIndexJsonParser(final Gson gson) {
        this.gson = gson;
    }

    public Optional<NugetResource> parseResourceFromIndexJson(final String indexJson, final ResourceType resourceType) {
        return parseResourceFromIndexJson(indexJson, resourceType.getType());
    }

    private Optional<NugetResource> parseResourceFromIndexJson(final String indexJson, final String resourceType) {
        final NugetIndex nugetIndex = gson.fromJson(indexJson, NugetIndex.class);
        final Optional<NugetResource> resource = resourceFromIndex(nugetIndex, resourceType);

        return resource;
    }

    private Optional<NugetResource> resourceFromIndex(final NugetIndex nugetIndex, final String resourceType) {
        final Optional<NugetResource> nugetResource = nugetIndex.getResources().stream()
                                                          .filter(p -> p.getType().equals(resourceType))
                                                          .findFirst();

        return nugetResource;
    }
}
