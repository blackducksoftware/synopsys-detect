/**
 * detectable
 *
 * Copyright (C) 2019 Black Duck Software, Inc.
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
package com.synopsys.integration.detectable.detectables.docker;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class DockerDetectableOptions {

    private final boolean dockerPathRequired;
    private final String suppliedDockerImage;
    private final String suppliedDockerTar;
    private final String dockerInspectorLoggingLevel;
    private final String dockerInspectorVersion;
    private final Map<String, String> additionalDockerProperties;

    public DockerDetectableOptions(final boolean dockerPathRequired, final String suppliedDockerImage,
        final String suppliedDockerTar, final String dockerInspectorLoggingLevel, final String dockerInspectorVersion, final Map<String, String> additionalDockerProperties) {
        this.dockerPathRequired = dockerPathRequired;
        this.suppliedDockerImage = suppliedDockerImage;
        this.suppliedDockerTar = suppliedDockerTar;
        this.dockerInspectorLoggingLevel = dockerInspectorLoggingLevel;
        this.dockerInspectorVersion = dockerInspectorVersion;
        this.additionalDockerProperties = additionalDockerProperties;
    }

    public boolean isDockerPathRequired() {
        return dockerPathRequired;
    }

    public String getSuppliedDockerImage() {
        return suppliedDockerImage;
    }

    public String getSuppliedDockerTar() {
        return suppliedDockerTar;
    }

    public boolean hasDockerImageOrTag() {
        return StringUtils.isNotBlank(getSuppliedDockerImage()) || StringUtils.isNotBlank(getSuppliedDockerTar());
    }

    public String getDockerInspectorLoggingLevel() {
        return dockerInspectorLoggingLevel;
    }

    public String getDockerInspectorVersion() {
        return dockerInspectorVersion;
    }

    public Map<String, String> getAdditionalDockerProperties() {
        return additionalDockerProperties;
    }
}
