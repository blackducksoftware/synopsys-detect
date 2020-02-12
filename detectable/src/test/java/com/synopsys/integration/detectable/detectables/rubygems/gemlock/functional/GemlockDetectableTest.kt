package com.synopsys.integration.detectable.detectables.rubygems.gemlock.functional

import com.synopsys.integration.bdio.model.Forge
import com.synopsys.integration.detectable.Detectable
import com.synopsys.integration.detectable.DetectableEnvironment
import com.synopsys.integration.detectable.Extraction
import com.synopsys.integration.detectable.functional.DetectableFunctionalTest
import com.synopsys.integration.detectable.util.graph.NameVersionGraphAssert
import org.junit.jupiter.api.Assertions

class GemlockDetectableTest : DetectableFunctionalTest("gemlock") {
    override fun setup() {
        addFiles {
            file("Gemfile.lock", """
                GEM
                  remote: https://rubygems.org/
                  specs:
                    RubyInline (3.12.4)
                          ZenTest (~> 4.3)
                    ZenTest (4.11.1)
                    activesupport (4.2.8)
                          thread_safe (~> 0.3, >= 0.3.4)
                    thread_safe (0.3.6)
                    cocoapods (1.2.1)
                          activesupport (>= 4.0.2, < 5)
                    cocoapods-keys (2.0.0)
                        osx_keychain
                    osx_keychain (1.0.1)
                          RubyInline (~> 3)
                          
                PLATFORMS
                  ruby

                DEPENDENCIES
                  cocoapods (>= 1.1.0)
                  cocoapods-keys
                  

                BUNDLED WITH
                   1.14.6
            """.trimIndent()
            )
        }
    }

    override fun create(environment: DetectableEnvironment): Detectable {
        return detectableFactory.createGemlockDetectable(environment)
    }

    override fun assert(extraction: Extraction) {
        Assertions.assertNotEquals(0, extraction.codeLocations.size, "A code location should have been generated.")

        val graphAssert = NameVersionGraphAssert(Forge.RUBYGEMS, extraction.codeLocations.first().dependencyGraph)
        graphAssert.hasRootDependency("cocoapods", "1.2.1")

        graphAssert.hasDependency("RubyInline", "3.12.4")
        graphAssert.hasParentChildRelationship("RubyInline", "3.12.4", "ZenTest", "4.11.1")

        graphAssert.hasParentChildRelationship("activesupport", "4.2.8", "thread_safe", "0.3.6")
        graphAssert.hasParentChildRelationship("cocoapods", "1.2.1", "activesupport", "4.2.8")
    }

}