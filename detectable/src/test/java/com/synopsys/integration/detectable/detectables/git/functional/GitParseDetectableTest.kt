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
package com.synopsys.integration.detectable.detectables.git.functional

import com.synopsys.integration.detectable.Detectable
import com.synopsys.integration.detectable.DetectableEnvironment
import com.synopsys.integration.detectable.Extraction
import com.synopsys.integration.detectable.functional.DetectableFunctionalTest
import org.junit.jupiter.api.Assertions

class GitParseDetectableTest : DetectableFunctionalTest("git-parse") {

    override fun setup() {
        addFiles {
            directory(".git") {
                file("config", """
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
                    [branch "test"]
                    	remote = origin
                    	merge = refs/heads/test
                """.trimIndent())

                file("HEAD", "ref: refs/heads/master\n")
            }
        }
    }

    override fun create(environment: DetectableEnvironment): Detectable {
        return detectableFactory.createGitParseDetectable(environment)
    }

    override fun assert(extraction: Extraction) {
        Assertions.assertEquals(0, extraction.codeLocations.size, "Git should not produce a dependency graph. It is for project info only.")
        Assertions.assertEquals("blackducksoftware/synopsys-detect", extraction.projectName)
        Assertions.assertEquals("master", extraction.projectVersion)
    }
}