/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.pnpm.lockfile.model;

import com.synopsys.integration.util.NameVersion;

public class PnpmPackageId {
    private final String name;
    private final String version;
    private final String id;

    public PnpmPackageId(String name, String version) {
        this.name = name;
        this.version = version;
        this.id = String.format("/%s/%s", name, version);
    }

    public PnpmPackageId(String id) {
        this.id = id;
        NameVersion nameVersion = parseNameVersionFromId(id);
        this.name = nameVersion.getName();
        this.version = nameVersion.getVersion();
    }

    private NameVersion parseNameVersionFromId(String id) {
        // ids follow format: /name/version, where name often contains slashes
        int indexOfLastSlash = id.lastIndexOf("/");
        String name = id.substring(1, indexOfLastSlash);
        String version = id.substring(indexOfLastSlash + 1);

        return new NameVersion(name, version);
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getPackageIndentifier() {
        return id;
    }
}
