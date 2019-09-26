/**
 * detectable
 *
 * Copyright (c) 2019 Synopsys, Inc.
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
package com.synopsys.integration.detectable.detectables.git.cli;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.exception.IntegrationException;

public class GitUrlParser {

    public String getRepoName(final String remoteUrlString) throws IntegrationException, MalformedURLException {
        final String remoteUrlPath;
        if (remoteUrlString.startsWith("ssh://")) {
            // Parses urls such as: ssh://user@synopsys.com:12345/blackducksoftware/synopsys-detect
            final int lastIndexOfSlash = remoteUrlString.lastIndexOf('/');
            final String projectName = remoteUrlString.substring(lastIndexOfSlash);
            final String remainder = remoteUrlString.substring(0, lastIndexOfSlash);
            final int remainderLastIndexOfSlash = remainder.lastIndexOf('/');
            final String organization = remainder.substring(remainderLastIndexOfSlash);
            remoteUrlPath = organization + projectName;
        } else if (remoteUrlString.contains("@")) {
            // Parses urls such as: git@github.com:blackducksoftware/synopsys-detect.git
            final String[] tokens = remoteUrlString.split(":");
            if (tokens.length != 2) {
                throw new IntegrationException(String.format("Failed to extract project name from: %s", remoteUrlString));
            }
            remoteUrlPath = tokens[1].trim();
        } else {
            // Parses urls such as: https://github.com/blackducksoftware/synopsys-detect
            final URL remoteURL = new URL(remoteUrlString);
            remoteUrlPath = remoteURL.getPath().trim();
        }

        return StringUtils.removeEnd(StringUtils.removeStart(remoteUrlPath, "/"), ".git");
    }
}
