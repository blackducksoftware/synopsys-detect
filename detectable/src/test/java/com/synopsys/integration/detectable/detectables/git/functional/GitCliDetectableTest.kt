package com.synopsys.integration.detectable.detectables.git.functional

import com.synopsys.integration.detectable.Detectable
import com.synopsys.integration.detectable.DetectableEnvironment
import com.synopsys.integration.detectable.Extraction
import com.synopsys.integration.detectable.detectable.executable.ExecutableOutput
import com.synopsys.integration.detectable.functional.DetectableFunctionalTest
import org.junit.jupiter.api.Assertions
import java.io.File

class GitCliDetectableTest : DetectableFunctionalTest("git-cli") {

    override fun setup() {
        addFiles {
            directory(".git")
        }

        addExecutableOutput(
                ExecutableOutput(
                        "git remote url",
                        0,
                        "https://github.com/blackducksoftware/synopsys-detect",
                        ""
                ), "git", "config", "--get", "remote.origin.url")

        addExecutableOutput(
                ExecutableOutput(
                        "git branch",
                        0,
                        "branch-version",
                        ""
                ), "git", "rev-parse", "--abbrev-ref", "HEAD")
    }

    override fun create(environment: DetectableEnvironment): Detectable {
        return detectableFactory.createGitCliDetectable(environment) { File("git") }
    }

    override fun assert(extraction: Extraction) {
        Assertions.assertEquals(0, extraction.codeLocations.size, "Git should not produce a dependency graph. It is for project info only.")
        Assertions.assertEquals("blackducksoftware/synopsys-detect", extraction.projectName)
        Assertions.assertEquals("branch-version", extraction.projectVersion)
    }
}