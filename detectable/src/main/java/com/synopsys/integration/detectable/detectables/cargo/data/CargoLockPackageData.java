package com.synopsys.integration.detectable.detectables.cargo.data;

import java.util.List;
import java.util.Optional;

import com.google.gson.annotations.SerializedName;

public class CargoLockPackageData {
    @SerializedName("name")
    private final String name;

    @SerializedName("version")
    private final String version;

    @SerializedName("source")
    private final String source;

    @SerializedName("checksum")
    private final String checksum;

    @SerializedName("dependencies")
    private final List<String> dependencies;

    public CargoLockPackageData(String name, String version, String source, String checksum, List<String> dependencies) {
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
