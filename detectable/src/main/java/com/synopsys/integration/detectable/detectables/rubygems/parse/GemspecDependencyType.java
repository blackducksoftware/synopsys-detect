package com.synopsys.integration.detectable.detectables.rubygems.parse;

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
