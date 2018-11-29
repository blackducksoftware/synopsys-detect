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
package com.blackducksoftware.integration.hub.detect.tool.polaris;

import java.io.File;

public class PolarisEnvironmentCheck {
    public static final String POLARIS_CONFIG_DIRECTORY = ".swip";
    public static final String POLARIS_ACCESS_TOKEN_FILENAME = ".access_token";

    public boolean canRun(final File homeDirectory) {
        if (null != homeDirectory && homeDirectory.exists() && homeDirectory.isDirectory()) {
            final File polarisConfig = new File(homeDirectory, POLARIS_CONFIG_DIRECTORY);
            if (null != polarisConfig && polarisConfig.exists() && polarisConfig.isDirectory()) {
                final File accessToken = new File(polarisConfig, POLARIS_ACCESS_TOKEN_FILENAME);
                if (null != accessToken && accessToken.exists() && accessToken.isFile() && accessToken.length() > 0) {
                    return true;
                }
            }
        }
        return false;
    }

}