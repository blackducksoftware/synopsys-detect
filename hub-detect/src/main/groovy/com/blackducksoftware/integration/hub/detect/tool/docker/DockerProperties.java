/**
 * hub-detect
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
package com.blackducksoftware.integration.hub.detect.tool.docker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.configuration.PropertyAuthority;

public class DockerProperties {
    private final DetectConfiguration detectConfiguration;

    public DockerProperties(final DetectConfiguration detectConfiguration) {
        this.detectConfiguration = detectConfiguration;
    }

    public void populatePropertiesFile(final File dockerPropertiesFile, final File outputDirectory) throws IOException {
        final Properties dockerProperties = new Properties();

        dockerProperties.setProperty("logging.level.com.synopsys", detectConfiguration.getProperty(DetectProperty.LOGGING_LEVEL_COM_BLACKDUCKSOFTWARE_INTEGRATION, PropertyAuthority.None));
        dockerProperties.setProperty("upload.bdio", "false");
        dockerProperties.setProperty("output.path", outputDirectory.getAbsolutePath());
        dockerProperties.setProperty("output.include.containerfilesystem", "true");
        dockerProperties.setProperty("phone.home", "false");
        dockerProperties.setProperty("caller.name", "Detect");

        final Map<String, String> additionalDockerProperties = detectConfiguration.getDockerProperties();
        additionalDockerProperties.forEach((key, value) -> dockerProperties.setProperty(key, value));

        dockerProperties.store(new FileOutputStream(dockerPropertiesFile), "");
    }
}
