/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
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
