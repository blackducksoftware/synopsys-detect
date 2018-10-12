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
package com.blackducksoftware.integration.hub.detect.bomtool.docker;

import java.io.File;
import java.util.List;

import org.springframework.util.CollectionUtils;

public class DockerInspectorInfo {
    private final File dockerInspectorJar;
    private final List<File> offlineTars;

    public DockerInspectorInfo(final File dockerInspectorJar,
            final List<File> offlineTars) {
        this.dockerInspectorJar = dockerInspectorJar;
        this.offlineTars = offlineTars;
    }

    public File getDockerInspectorJar() {
        return dockerInspectorJar;
    }

    public boolean isOffline() {
        return !CollectionUtils.isEmpty(offlineTars);
    }

    public List<File> getOfflineTars() {
        return offlineTars;
    }
}
