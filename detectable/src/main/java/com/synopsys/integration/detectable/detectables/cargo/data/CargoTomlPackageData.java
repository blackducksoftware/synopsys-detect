package com.synopsys.integration.detectable.detectables.cargo.data;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.google.gson.annotations.SerializedName;

public class CargoTomlPackageData {
    @Nullable
    @SerializedName("name")
    private final String name;

    @Nullable
    @SerializedName("version")
    private final String version;

    public CargoTomlPackageData(@Nullable String name, @Nullable String version) {
        this.name = name;
        this.version = version;
    }

    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }

    public Optional<String> getVersion() {
        return Optional.ofNullable(version);
    }
}
