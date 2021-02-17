/*
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
package com.synopsys.integration.detectable.detectables.git.parsing.model;

import java.util.Map;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

public class GitConfigNode {
    private final String type;
    @Nullable
    private final String name;
    private final Map<String, String> properties;

    public GitConfigNode(final String type, final Map<String, String> properties) {
        this(type, null, properties);
    }

    public GitConfigNode(final String type, @Nullable final String name, final Map<String, String> properties) {
        this.type = type;
        this.name = name;
        this.properties = properties;
    }

    public String getType() {
        return type;
    }

    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }

    public Optional<String> getProperty(final String propertyKey) {
        return Optional.ofNullable(properties.get(propertyKey));
    }
}
