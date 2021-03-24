/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectable.util;

import java.util.Deque;
import java.util.LinkedList;

import com.synopsys.integration.bdio.model.dependency.Dependency;

public class DependencyHistory {
    private final Deque<Dependency> dependencyStack = new LinkedList<>();

    public void clearDependenciesDeeperThan(final int dependencyLevel) throws IllegalStateException {
        if (dependencyLevel > dependencyStack.size()) {
            throw new IllegalStateException(
                String.format("Level of dependency should be less than or equal to %s but was %s. Treating the dependency as though level was %s.", dependencyStack.size(), dependencyLevel, dependencyStack.size()));
        }

        final int levelDelta = (dependencyStack.size() - dependencyLevel);
        for (int levels = 0; levels < levelDelta; levels++) {
            dependencyStack.pop();
        }
    }

    public void clear() {
        dependencyStack.clear();
    }

    public void add(final Dependency dependency) {
        dependencyStack.push(dependency);
    }

    public boolean isEmpty() {
        return dependencyStack.isEmpty();
    }

    public Dependency getLastDependency() {
        return dependencyStack.peek();
    }

}
