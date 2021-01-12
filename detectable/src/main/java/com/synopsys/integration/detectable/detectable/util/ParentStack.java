/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.synopsys.integration.detectable.detectable.util;

import java.util.Deque;
import java.util.LinkedList;

public class ParentStack<T> {
    private final Deque<T> stack = new LinkedList<>();

    public void clearDeeperThan(final int dependencyLevel) throws IllegalStateException {
        if (dependencyLevel > stack.size()) {
            throw new IllegalStateException(
                String.format("Level of dependency should be less than or equal to %s but was %s. Treating it as though level was %s.", stack.size(), dependencyLevel, stack.size()));
        }

        final int levelDelta = (stack.size() - dependencyLevel);
        for (int levels = 0; levels < levelDelta; levels++) {
            stack.pop();
        }
    }

    public void clear() {
        stack.clear();
    }

    public void add(final T dependency) {
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
