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
package com.synopsys.integration.detect.workflow.nameversion.git.unit;

import java.net.MalformedURLException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.workflow.nameversion.git.cli.GitUrlParser;

class GitUrlParserTest {
    @Test
    void sshUrl() throws MalformedURLException {
        GitUrlParser gitUrlParser = new GitUrlParser();
        String remoteUrl = "ssh://user@synopsys.com:12345/blackducksoftware/synopsys-detect";
        String repoName = gitUrlParser.getRepoName(remoteUrl);

        Assertions.assertEquals("blackducksoftware/synopsys-detect", repoName);
    }

    @Test
    void gitUrl() throws MalformedURLException {
        GitUrlParser gitUrlParser = new GitUrlParser();
        String remoteUrl = "git://git.yoctoproject.org/poky.git";
        String repoName = gitUrlParser.getRepoName(remoteUrl);

        Assertions.assertEquals("git.yoctoproject.org/poky", repoName);
    }

    @Test
    void gitAtUrl() throws MalformedURLException {
        GitUrlParser gitUrlParser = new GitUrlParser();
        String remoteUrl = "git@github.com:blackducksoftware/synopsys-detect.git";
        String repoName = gitUrlParser.getRepoName(remoteUrl);

        Assertions.assertEquals("blackducksoftware/synopsys-detect", repoName);
    }

    @Test
    void httpsUrl() throws MalformedURLException {
        GitUrlParser gitUrlParser = new GitUrlParser();
        String remoteUrl = "https://github.com/blackducksoftware/synopsys-detect";
        String repoName = gitUrlParser.getRepoName(remoteUrl);

        Assertions.assertEquals("blackducksoftware/synopsys-detect", repoName);
    }

    @Test
    void httpsEncodedUsernamePasswordUrl() throws MalformedURLException {
        GitUrlParser gitUrlParser = new GitUrlParser();
        String remoteUrl = "https://USERNAME:PASSWORD@SERVER/test/path/to/blackducksoftware/synopsys-detect.git";
        String repoName = gitUrlParser.getRepoName(remoteUrl);

        Assertions.assertEquals("blackducksoftware/synopsys-detect", repoName);
    }
}