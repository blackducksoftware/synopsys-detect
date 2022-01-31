package com.synopsys.integration.detectable.detectables.bitbake.model;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class BitbakeNode {
    private final String name;
    private String version = null;
    private String layer = null;
    private final Set<String> children = new HashSet<>();

    public BitbakeNode(String name) {this.name = name;}

    public void addChild(String child) {
        this.children.add(child);
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setLayer(String layer) {
        this.layer = layer;
    }

    public String getName() {
        return name;
    }

    public Optional<String> getVersion() {
        return Optional.ofNullable(version);
    }

    public Optional<String> getLayer() {
        return Optional.ofNullable(layer);
    }

    public Set<String> getChildren() {
        return children;
    }

}
