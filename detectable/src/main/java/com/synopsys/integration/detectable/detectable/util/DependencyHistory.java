package com.synopsys.integration.detectable.detectable.util;

import java.util.Deque;
import java.util.LinkedList;

import com.synopsys.integration.bdio.model.dependency.Dependency;

public class DependencyHistory {
    private final Deque<Dependency> dependencyStack = new LinkedList<>();

    public void clearDependenciesDeeperThan(int dependencyLevel) throws IllegalStateException {
        if (dependencyLevel > dependencyStack.size()) {
            throw new IllegalStateException(
                String.format(
                    "Level of dependency should be less than or equal to %s but was %s. Treating the dependency as though level was %s.",
                    dependencyStack.size(),
                    dependencyLevel,
                    dependencyStack.size()
                ));
        }

        int levelDelta = (dependencyStack.size() - dependencyLevel);
        for (int levels = 0; levels < levelDelta; levels++) {
            dependencyStack.pop();
        }
    }

    public void clear() {
        dependencyStack.clear();
    }

    public void add(Dependency dependency) {
        dependencyStack.push(dependency);
    }

    public boolean isEmpty() {
        return dependencyStack.isEmpty();
    }

    public Dependency getLastDependency() {
        return dependencyStack.peek();
    }

}
