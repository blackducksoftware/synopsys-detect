/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.clang.packagemanager.resolver;

public class RpmPackage {
    private final String epoch;
    private final String name;
    private final String version;
    private final String arch;

    public RpmPackage(final String epoch, final String name, final String version, final String arch) {
        super();
        this.epoch = epoch;
        this.name = name;
        this.version = version;
        this.arch = arch;
    }

    public String getEpoch() {
        return epoch;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getArch() {
        return arch;
    }
}
