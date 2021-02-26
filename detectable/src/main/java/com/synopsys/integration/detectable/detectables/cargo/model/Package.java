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

public class Package {
    private String name;
    private String version;
    private String source;
    private String checksum;
    private List<String> dependencies;

    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Optional<String> getVersion() {
        return Optional.ofNullable(version);
    }

    public void setVersion(final String version) {
        this.version = version;
    }

    public Optional<String> getSource() {
        return Optional.ofNullable(source);
    }

    public void setSource(final String source) {
        this.source = source;
    }

    public Optional<String> getChecksum() {
        return Optional.ofNullable(checksum);
    }

    public void setChecksum(final String checksum) {
        this.checksum = checksum;
    }

    public Optional<List<String>> getDependencies() {
        return Optional.ofNullable(dependencies);
    }

    public void setDependencies(final List<String> dependencies) {
        this.dependencies = dependencies;
    }
}
