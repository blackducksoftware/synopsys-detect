/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
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
