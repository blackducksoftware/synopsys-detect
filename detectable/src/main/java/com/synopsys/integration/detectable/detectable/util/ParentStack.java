package com.synopsys.integration.detectable.detectable.util;

import java.util.Deque;
import java.util.LinkedList;

public class ParentStack<T> {
    private final Deque<T> stack = new LinkedList<>();

    public void clearDeeperThan(int dependencyLevel) throws IllegalStateException {
        if (dependencyLevel > stack.size()) {
            throw new IllegalStateException(
                String.format(
                    "Level of dependency should be less than or equal to %s but was %s. Treating it as though level was %s.",
                    stack.size(),
                    dependencyLevel,
                    stack.size()
                ));
        }

        int levelDelta = (stack.size() - dependencyLevel);
        for (int levels = 0; levels < levelDelta; levels++) {
            stack.pop();
        }
    }

    public void clear() {
        stack.clear();
    }

    public void add(T dependency) {
        stack.push(dependency);
    }

    public boolean isEmpty() {
        return stack.isEmpty();
    }

    public T getCurrent() {
        return stack.peek();
    }

    public int size() {
        return stack.size();
    }

}
