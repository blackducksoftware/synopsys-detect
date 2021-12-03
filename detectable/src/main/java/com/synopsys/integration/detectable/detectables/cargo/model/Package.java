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

    public void setName(String name) {
        this.name = name;
    }

    public Optional<String> getVersion() {
        return Optional.ofNullable(version);
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Optional<String> getSource() {
        return Optional.ofNullable(source);
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Optional<String> getChecksum() {
        return Optional.ofNullable(checksum);
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public Optional<List<String>> getDependencies() {
        return Optional.ofNullable(dependencies);
    }

    public void setDependencies(List<String> dependencies) {
        this.dependencies = dependencies;
    }
}
