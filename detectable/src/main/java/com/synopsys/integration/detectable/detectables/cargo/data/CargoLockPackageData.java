package com.synopsys.integration.detectable.detectables.cargo.data;

import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.google.gson.annotations.SerializedName;

public class CargoLockPackageData {
    @Nullable
    @SerializedName("name")
    private final String name;

    @Nullable
    @SerializedName("version")
    private final String version;

    @Nullable
    @SerializedName("source")
    private final String source;

    @Nullable
    @SerializedName("checksum")
    private final String checksum;

    @Nullable
    @SerializedName("dependencies")
    private final List<String> dependencies;

    public CargoLockPackageData(@Nullable String name, @Nullable String version, @Nullable String source, @Nullable String checksum, @Nullable List<String> dependencies) {
        this.name = name;
        this.version = version;
        this.source = source;
        this.checksum = checksum;
        this.dependencies = dependencies;
    }

    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }

    public Optional<String> getVersion() {
        return Optional.ofNullable(version);
    }

    public Optional<String> getSource() {
        return Optional.ofNullable(source);
    }

    public Optional<String> getChecksum() {
        return Optional.ofNullable(checksum);
    }

    public Optional<List<String>> getDependencies() {
        return Optional.ofNullable(dependencies);
    }
}
