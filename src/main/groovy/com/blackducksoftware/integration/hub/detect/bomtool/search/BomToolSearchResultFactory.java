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
package com.blackducksoftware.integration.hub.detect.bomtool.search;

import java.io.File;

public class BomToolSearchResultFactory {
    public static BomToolSearchResult createApplies(File searchedDirectory) {
        return new BomToolSearchResult(true, searchedDirectory);
    }

    public static BomToolSearchResult createDoesNotApply() {
        return new BomToolSearchResult(false, null);
    }

    public static NpmBomToolSearchResult createNpmApplies(final File searchedDirectory, final String npmExePath, final File packageLockJson, final File shrinkwrapJson) {
        return new NpmBomToolSearchResult(true, searchedDirectory, npmExePath, packageLockJson, shrinkwrapJson);
    }

    public static NpmBomToolSearchResult createNpmDoesNotApply() {
        return new NpmBomToolSearchResult(false, null, null, null, null);
    }

}
