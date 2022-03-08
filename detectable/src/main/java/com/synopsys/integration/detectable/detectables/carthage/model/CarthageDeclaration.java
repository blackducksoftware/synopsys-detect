package com.synopsys.integration.detectable.detectables.carthage.model;

// eg. github "realm/realm-cocoa" "v10.7.2"
public class CarthageDeclaration {
    private final String origin;
    private final String name;
    private final String version;

    public CarthageDeclaration(String origin, String name, String version) {
        this.origin = origin;
        this.name = name;
        this.version = version;
    }

    public String getOrigin() {
        return origin;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }
}
