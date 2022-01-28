package com.synopsys.integration.detectable.detectables.cargo.data;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.google.gson.annotations.SerializedName;

public class CargoTomlData {
    @Nullable
    @SerializedName("package")
    private final CargoTomlPackageData cargoTomlPackageData;

    public CargoTomlData(@Nullable CargoTomlPackageData cargoTomlPackageData) {this.cargoTomlPackageData = cargoTomlPackageData;}

    public Optional<CargoTomlPackageData> getCargoTomlPackage() {
        return Optional.ofNullable(cargoTomlPackageData);
    }
}
