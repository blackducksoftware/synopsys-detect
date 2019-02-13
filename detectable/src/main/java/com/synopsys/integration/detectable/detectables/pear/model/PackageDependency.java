package com.synopsys.integration.detectable.detectables.pear.model;

public class PackageDependency {
    private final String name;
    private final boolean required;

    public PackageDependency(final String name, final boolean required) {
        this.name = name;
        this.required = required;
    }

    public String getName() {
        return name;
    }

    public boolean isRequired() {
        return required;
    }
}
