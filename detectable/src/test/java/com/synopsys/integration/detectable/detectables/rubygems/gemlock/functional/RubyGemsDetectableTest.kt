package com.synopsys.integration.detectable.detectables.rubygems.gemlock.functional

import com.synopsys.integration.detectable.Detectable
import com.synopsys.integration.detectable.DetectableEnvironment
import com.synopsys.integration.detectable.Extraction
import com.synopsys.integration.detectable.detectable.executable.ExecutableOutput
import com.synopsys.integration.detectable.functional.DetectableFunctionalTest

class RubyGemsDetectableTest : DetectableFunctionalTest("rubygems") {
    override fun setup() {
        addFiles {
            file("Gemfile.lock", """
                GEM
                  remote: https://rubygems.org/
                  specs:
            """.trimIndent()
            )
        }

        addExecutableOutput(
                ExecutableOutput()
        )

    }

    override fun create(environment: DetectableEnvironment): Detectable {
        return detectableFactory.createGemlockDetectable(environment)
    }

    override fun assert(extraction: Extraction) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}