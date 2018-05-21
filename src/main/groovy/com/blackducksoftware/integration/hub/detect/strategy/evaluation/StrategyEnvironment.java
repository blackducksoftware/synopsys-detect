/**
 * hub-detect
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
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
package com.blackducksoftware.integration.hub.detect.strategy.evaluation;

import java.io.File;
import java.util.Set;

import com.blackducksoftware.integration.hub.detect.strategy.Strategy;
import com.blackducksoftware.integration.util.ExcludedIncludedFilter;

public class StrategyEnvironment {

    private final File directory;
    private final Set<Strategy> appliedToDirectory;
    private final Set<Strategy> appliedToParent;
    private final int depth;
    private final ExcludedIncludedFilter bomToolFilter;

    public StrategyEnvironment(final File directory, final Set<Strategy> appliedToDirectory, final Set<Strategy> appliedToParent, final int depth, final ExcludedIncludedFilter bomToolFilter) {
        this.directory = directory;
        this.appliedToDirectory = appliedToDirectory;
        this.appliedToParent = appliedToParent;
        this.depth = depth;
        this.bomToolFilter = bomToolFilter;
    }

    public File getDirectory() {
        return directory;
    }

    public Set<Strategy> getAppliedToDirectory() {
        return appliedToDirectory;
    }

    public Set<Strategy> getAppliedToParent() {
        return appliedToParent;
    }

    public int getDepth() {
        return depth;
    }

    public ExcludedIncludedFilter getBomToolFilter() {
        return bomToolFilter;
    }
}
