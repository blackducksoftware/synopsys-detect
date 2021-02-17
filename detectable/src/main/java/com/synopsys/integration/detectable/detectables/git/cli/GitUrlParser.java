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
package com.synopsys.integration.detectable.detectables.git.cli;

import java.net.MalformedURLException;

import org.apache.commons.lang3.StringUtils;

public class GitUrlParser {
    // Parses urls such as: https://github.com/blackducksoftware/synopsys-detect
    public String getRepoName(final String remoteUrlString) throws MalformedURLException {
        final String[] pieces = remoteUrlString.split("[/:]");
        if (pieces.length >= 2) {
            final String organization = pieces[pieces.length - 2];
            final String repo = pieces[pieces.length - 1];
            final String name = String.format("%s/%s", organization, repo);
            return StringUtils.removeEnd(StringUtils.removeStart(name, "/"), ".git");
        } else {
            throw new MalformedURLException("Failed to extract repository name from url. Not logging url for security.");
        }
    }
}
