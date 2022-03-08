package com.synopsys.integration.detectable.detectables.npm.lockfile.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.detectable.util.ExternalIdCreator;

public class NpmDependency extends Dependency {
    private final boolean devDependency;
    private final boolean peerDependency;

    public NpmDependency(String name, String version, ExternalId externalId, boolean devDependency, boolean peerDependency) {
        super(name, version, externalId);
        this.devDependency = devDependency;
        this.peerDependency = peerDependency;
    }

    public NpmDependency(String name, String version, boolean devDependency, boolean peerDependency) {
        super(name, version, ExternalIdCreator.nameVersion(Forge.NPMJS, name, version));
        this.devDependency = devDependency;
        this.peerDependency = peerDependency;
    }

    private NpmDependency parent;
    private final List<NpmRequires> requires = new ArrayList<>();
    private final List<NpmDependency> dependencies = new ArrayList<>();

    public Optional<NpmDependency> getParent() {
        return Optional.ofNullable(parent);
    }

    public void setParent(NpmDependency parent) {
        this.parent = parent;
    }

    public void addAllRequires(Collection<NpmRequires> required) {
        this.requires.addAll(required);
    }

    public void addAllDependencies(Collection<NpmDependency> dependencies) {
        this.dependencies.addAll(dependencies);
    }

    public List<NpmRequires> getRequires() {
        return requires;
    }

    public List<NpmDependency> getDependencies() {
        return dependencies;
    }

    public boolean isDevDependency() {
        return devDependency;
    }

    public boolean isPeerDependency() {
        return peerDependency;
    }
}
