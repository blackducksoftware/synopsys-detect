package com.synopsys.integration.detectable.detectables.rubygems.gemspec.parse;

public enum GemspecDependencyType {
    NORMAL(".add_dependency"),

    RUNTIME(".add_runtime_dependency"),

    DEVELOPMENT(".add_development_dependency");

    private final String token;

    GemspecDependencyType(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
