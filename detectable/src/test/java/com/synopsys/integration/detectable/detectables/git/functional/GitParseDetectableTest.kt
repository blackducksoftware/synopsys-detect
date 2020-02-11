package com.synopsys.integration.detectable.detectables.git.functional

import com.synopsys.integration.detectable.Detectable
import com.synopsys.integration.detectable.DetectableEnvironment
import com.synopsys.integration.detectable.Extraction
import com.synopsys.integration.detectable.functional.DetectableFunctionalTest
import org.junit.jupiter.api.Assertions

class GitParseDetectableTest : DetectableFunctionalTest("git-cli") {

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