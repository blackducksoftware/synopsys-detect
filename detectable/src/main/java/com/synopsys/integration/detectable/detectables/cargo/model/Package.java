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
package com.synopsys.integration.detectable.detectables.cargo.model;

import java.util.List;
import java.util.Optional;

public class Package {
    private String name;
    private String version;
    private String source;
    private String checksum;
    private List<String> dependencies;

    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Optional<String> getVersion() {
        return Optional.ofNullable(version);
    }

    public void setVersion(final String version) {
        this.version = version;
    }

    public Optional<String> getSource() {
        return Optional.ofNullable(source);
    }

    public void setSource(final String source) {
        this.source = source;
    }

    public Optional<String> getChecksum() {
        return Optional.ofNullable(checksum);
    }

    public void setChecksum(final String checksum) {
        this.checksum = checksum;
    }

    public Optional<List<String>> getDependencies() {
        return Optional.ofNullable(dependencies);
    }

    public void setDependencies(final List<String> dependencies) {
        this.dependencies = dependencies;
    }
}
