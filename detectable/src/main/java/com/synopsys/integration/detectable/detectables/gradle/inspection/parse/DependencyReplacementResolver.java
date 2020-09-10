package com.synopsys.integration.detectable.detectables.gradle.inspection.parse;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.bdio.model.dependency.Dependency;

public class DependencyReplacementResolver {
    @Nullable
    private final DependencyReplacementResolver parentResolver;
    private final Map<Dependency, Dependency> replacementMap;

    public static DependencyReplacementResolver createFromParentResolver(DependencyReplacementResolver dependencyReplacementResolver) {
        return new DependencyReplacementResolver(dependencyReplacementResolver);
    }

    public static DependencyReplacementResolver createRootResolver() {
        return new DependencyReplacementResolver(null);
    }

    public DependencyReplacementResolver(@Nullable DependencyReplacementResolver parentResolver) {
        this.parentResolver = parentResolver;
        this.replacementMap = new HashMap<>();
    }

    public void addReplacementData(Dependency replaced, Dependency replacement) {
        replacementMap.put(replaced, replacement);
    }

    public Optional<Dependency> getReplacement(Dependency dependency) {
        Dependency replacement = null;

        if (parentResolver != null) {
            replacement = parentResolver.getReplacement(dependency).orElse(null);
        }

        if (replacement == null) {
            replacement = replacementMap.get(dependency);
            if (parentResolver != null && replacement != null) {
                replacement = parentResolver.getReplacement(replacement).orElse(replacement);
            }
        }

        return Optional.ofNullable(replacement);

    }
}
