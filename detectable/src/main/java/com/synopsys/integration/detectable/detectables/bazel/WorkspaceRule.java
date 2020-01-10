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

import com.synopsys.integration.exception.IntegrationException;

public enum WorkspaceRule {
    MAVEN_JAR("maven_jar"),
    MAVEN_INSTALL("maven_install"),
    UNKNOWN(null);

    private String name;

    WorkspaceRule(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static WorkspaceRule lookup(final String targetName) throws IntegrationException {
        final String trimmedTargetName = targetName.trim();
        for (final WorkspaceRule candidate : WorkspaceRule.values()) {
            if ((candidate != WorkspaceRule.UNKNOWN) && candidate.getName().equals(trimmedTargetName)) {
                return candidate;
            }
        }
        throw new IntegrationException(String.format("Unsupported bazel workspace rule: %s", targetName));
    }
}
