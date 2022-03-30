package com.synopsys.integration.detectable.detectables.gradle.inspection.model;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.bdio.graph.builder.LazyId;

public class ReplacedGradleGav implements GradleGavId {
    private final String group;
    private final String artifact;
    private final String version;

    public ReplacedGradleGav(String group, String artifact) {
        this(group, artifact, null);
    }

    public ReplacedGradleGav(String group, String artifact, @Nullable String version) {
        this.group = group;
        this.artifact = artifact;
        this.version = version;
    }

    public String getGroup() {
        return group;
    }

    public String getArtifact() {
        return artifact;
    }

    public Optional<String> getVersion() {
        return Optional.ofNullable(version);
    }

    @Override
    public LazyId toDependencyId() {
        String id = String.format("%s:%s%s", getGroup(), getArtifact(), getVersion()
            .map(it -> ":" + it)
            .orElse(""));
        return LazyId.fromString(id);
    }
}
