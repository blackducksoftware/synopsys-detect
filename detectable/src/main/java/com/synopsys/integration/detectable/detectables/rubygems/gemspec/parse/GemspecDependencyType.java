/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.rubygems.gemspec.parse;

public enum GemspecDependencyType {
    NORMAL(".add_dependency"),

    RUNTIME(".add_runtime_dependency"),

    DEVELOPMENT(".add_development_dependency");

    private final String token;

    GemspecDependencyType(final String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
