/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.bitbake.model;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class BitbakeNode {
    private final String name;
    private String version = null;
    private final Set<String> children = new HashSet<>();

    public BitbakeNode(final String name) {this.name = name;}

    public void addChild(final String child) {
        this.children.add(child);
    }

    public void setVersion(final String version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public Optional<String> getVersion() {
        return Optional.ofNullable(version);
    }

    public Set<String> getChildren() {
        return children;
    }

}
