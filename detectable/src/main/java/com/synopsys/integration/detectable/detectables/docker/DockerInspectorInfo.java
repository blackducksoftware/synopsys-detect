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
package com.synopsys.integration.detectable.detectables.docker;

import java.io.File;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

public class DockerInspectorInfo {
    private final File dockerInspectorJar;
    private final List<File> airGapInspectorImageTarFiles;

    public DockerInspectorInfo(final File dockerInspectorJar) {
        this.dockerInspectorJar = dockerInspectorJar;
        this.airGapInspectorImageTarFiles = null;
    }

    public DockerInspectorInfo(final File dockerInspectorJar,
        final List<File> airGapInspectorImageTarFiles) {
        this.dockerInspectorJar = dockerInspectorJar;
        this.airGapInspectorImageTarFiles = airGapInspectorImageTarFiles;
    }

    public File getDockerInspectorJar() {
        return dockerInspectorJar;
    }

    public boolean hasAirGapImageFiles() {
        return !CollectionUtils.isEmpty(airGapInspectorImageTarFiles);
    }

    public List<File> getAirGapInspectorImageTarFiles() {
        return airGapInspectorImageTarFiles;
    }
}
