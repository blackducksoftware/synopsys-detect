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
package com.synopsys.integration.detectable.detectables.git.unit

import com.synopsys.integration.detectable.detectables.git.parsing.parse.GitFileParser
import com.synopsys.integration.detectable.util.TestExtensionFunctions.Companion.toInputStream
import org.apache.commons.io.IOUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.IOException
import java.nio.charset.StandardCharsets

internal class GitFileParserTest {
    @Test
    @Throws(IOException::class)
    fun parseHeadFile() {
        val gitFileParser = GitFileParser()
        val gitHeadContent = "ref: refs/heads/master\n"
        val inputStream = IOUtils.toInputStream(gitHeadContent, StandardCharsets.UTF_8)
        val head = gitFileParser.parseGitHead(inputStream)
        Assertions.assertEquals("refs/heads/master", head)
    }

    @Test
    @Throws(IOException::class)
    fun parseGitConfig() {
        val gitFileParser = GitFileParser()
        val inputStream = """
[core]
	repositoryformatversion = 0
	filemode = true
	bare = false
	logallrefupdates = true
	ignorecase = true
	precomposeunicode = true
[remote "origin"]
	url = https://github.com/blackducksoftware/synopsys-detect.git
	fetch = +refs/heads/*:refs/remotes/origin/*
[branch "master"]
	remote = origin
	merge = refs/heads/master
[branch "master-backup"]
	remote = origin
	merge = refs/heads/master-backup
[branch "6.0.0-actual"]
	remote = origin
	merge = refs/heads/6.0.0-actual
[branch "deployment-test"]
	remote = origin
	merge = refs/heads/deployment-test
[branch "verify-config"]
	remote = origin
	merge = refs/heads/verify-config
""".toInputStream()

        val gitConfigElements = gitFileParser.parseGitConfig(inputStream)
        Assertions.assertEquals(7, gitConfigElements.size)

        val gitConfigCores = gitConfigElements.filter { it.elementType == "core" }
        Assertions.assertEquals(1, gitConfigCores.size)

        val gitConfigRemotes = gitConfigElements.filter { it.elementType == "remote" }
        Assertions.assertEquals(1, gitConfigRemotes.size)

        val gitConfigBranches = gitConfigElements.filter { it.elementType == "branch" }
        Assertions.assertEquals(5, gitConfigBranches.size)

        val remoteOrigin = gitConfigElements
                .filter { it.elementType == "remote" }
                .filter { it.name.isPresent }
                .first { it.name.get() == "origin" }

        Assertions.assertNotNull(remoteOrigin)
        Assertions.assertTrue(remoteOrigin.containsKey("fetch"))

        val fetch = remoteOrigin.getProperty("fetch")
        Assertions.assertEquals("+refs/heads/*:refs/remotes/origin/*", fetch)
    }
}