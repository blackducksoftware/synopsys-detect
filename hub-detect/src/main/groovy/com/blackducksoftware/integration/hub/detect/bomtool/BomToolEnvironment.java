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
package com.blackducksoftware.integration.hub.detect.bomtool;

import java.io.File;
import java.util.Set;

import com.blackducksoftware.integration.hub.detect.OverridableExcludedIncludedFilter;

public class BomToolEnvironment {

    private final File directory;
    private final Set<BomToolType> appliedToParent;
    private final int depth;
    private final OverridableExcludedIncludedFilter bomToolFilter;
    private final boolean forceNestedSearch;

    public BomToolEnvironment(final File directory, final Set<BomToolType> appliedToParent, final int depth, final OverridableExcludedIncludedFilter bomToolFilter, final boolean forceNestedSearch) {
        this.directory = directory;
        this.appliedToParent = appliedToParent;
        this.depth = depth;
        this.bomToolFilter = bomToolFilter;
        this.forceNestedSearch = forceNestedSearch;
    }

    public File getDirectory() {
        return directory;
    }

    public Set<BomToolType> getAppliedToParent() {
        return appliedToParent;
    }

    public int getDepth() {
        return depth;
    }

    public OverridableExcludedIncludedFilter getBomToolFilter() {
        return bomToolFilter;
    }

    public boolean getForceNestedSearch() {
        return forceNestedSearch;
    }
}
