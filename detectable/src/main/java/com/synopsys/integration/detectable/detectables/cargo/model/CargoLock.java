package com.synopsys.integration.detectable.detectables.cargo.model;

import java.util.List;
import java.util.Optional;

import com.google.gson.annotations.SerializedName;

public class CargoLock {
    @SerializedName("package")
    private List<CargoLockPackage> packages;

    public Optional<List<CargoLockPackage>> getPackages() {
        return Optional.ofNullable(packages);
    }
}
