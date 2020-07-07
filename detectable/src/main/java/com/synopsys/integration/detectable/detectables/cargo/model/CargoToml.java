package com.synopsys.integration.detectable.detectables.cargo.model;

import java.util.Optional;

import com.google.gson.annotations.SerializedName;

public class CargoToml {

    @SerializedName("package")
    Package cargoPackage;

    public Optional<Package> getPackage() {
        return Optional.ofNullable(cargoPackage);
    }
}
