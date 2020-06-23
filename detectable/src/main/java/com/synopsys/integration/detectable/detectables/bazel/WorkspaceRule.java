/**
 * detectable
 *
 * Copyright (c) 2020 Synopsys, Inc.
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
package com.synopsys.integration.detectable.detectables.bazel;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum WorkspaceRule {
    MAVEN_JAR("maven_jar"),
    MAVEN_INSTALL("maven_install"),
    HASKELL_CABAL_LIBRARY("haskell_cabal_library"),
    UNSPECIFIED(null); // leaving here to avoid a breaking change

    private final String name;

    WorkspaceRule(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    protected static final List<String> POSSIBLE_NAMES = Arrays.stream(WorkspaceRule.values()).map(WorkspaceRule::name).collect(Collectors.toList());

    public static List<String> getPossibleNames() {
        return POSSIBLE_NAMES;
    }
}
