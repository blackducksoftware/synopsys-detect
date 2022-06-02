package com.synopsys.integration.detectable.detectables.swift.cli.model;

import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.google.gson.annotations.SerializedName;

public class SwiftPackage {
    @SerializedName("name")
    private final String name;

    @SerializedName("version")
    private final String version;

    @SerializedName("url")
    private final String url;

    @SerializedName("dependencies")
    private final List<SwiftPackage> dependencies;

    // This field was introduced in Swift 5.6
    @Nullable
    @SerializedName("identity")
    private final String identity;

    public SwiftPackage(String name, String version, List<SwiftPackage> dependencies, @Nullable String identity, @Nullable String url) {
        this.name = name;
        this.version = version;
        this.dependencies = dependencies;
        this.identity = identity;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public List<SwiftPackage> getDependencies() {
        return dependencies;
    }

    public Optional<String> getIdentity() {
        return Optional.ofNullable(identity);
    }

    public String getUrl() {
        return url;
    }
}
