package com.synopsys.integration.detectable.detectables.cargo.data;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.google.gson.annotations.SerializedName;

public class CargoToml {
    @Nullable
    @SerializedName("package")
    private final CargoTomlPackage cargoTomlPackage;

    public CargoToml(@Nullable CargoTomlPackage cargoTomlPackage) {this.cargoTomlPackage = cargoTomlPackage;}

    public Optional<CargoTomlPackage> getCargoTomlPackage() {
        return Optional.ofNullable(cargoTomlPackage);
    }
}
