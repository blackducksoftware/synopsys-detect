package com.synopsys.integration.detectable.detectables.cargo.model;

import java.util.List;
import java.util.Optional;

import com.google.gson.annotations.SerializedName;

public class CargoLock {
    @SerializedName("package")
    private List<Package> packages;

    public Optional<List<Package>> getPackages() {
        return Optional.ofNullable(packages);
    }
}
