/**
 * detect-configuration
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
package com.blackducksoftware.integration.hub.detect.property;

public enum BomToolSearchExcludedDirectories {
    BIN("bin"),
    BUILD("build"),
    DOT_GIT(".git"),
    DOT_GRADLE(".gradle"),
    NODE_MODULES("node_modules"),
    OUT("out"),
    PACKAGES("packages"),
    TARGET("target");

    public static final String DIRECTORY_NAMES = "bin, build, .git, .gradle, node_modules, out, packages, target";

    private final String directoryName;

    private BomToolSearchExcludedDirectories(final String directoryName) {
        this.directoryName = directoryName;
    }

    public String getDirectoryName() {
        return directoryName;
    }

}
