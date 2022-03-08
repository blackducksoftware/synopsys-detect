package com.synopsys.integration.detectable.detectables.rubygems.gemlock.functional;

import java.io.IOException;
import java.nio.file.Paths;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.functional.DetectableFunctionalTest;
import com.synopsys.integration.detectable.util.graph.NameVersionGraphAssert;

public class GemlockDetectableTest extends DetectableFunctionalTest {

    public GemlockDetectableTest() throws IOException {
        super("gemlock");
    }

    @Override
    public void setup() throws IOException {
        addFile(
            Paths.get("Gemfile.lock"),
            "GEM",
            "  remote:https://rubygems.org/",
            "  specs:",
            "    RubyInline (3.12.4)",
            "      ZenTest (~>4.3)",
            "    ZenTest (4.11.1)",
            "    activesupport (4.2.8)",
            "      thread_safe (~>0.3,>=0.3.4)",
            "    thread_safe (0.3.6)",
            "    cocoapods (1.2.1)",
            "      activesupport (>=4.0.2,< 5)",
            "    cocoapods-keys (2.0.0)",
            "      osx_keychain",
            "    osx_keychain (1.0.1)",
            "      RubyInline (~>3)",
            "",
            "PLATFORMS",
            "  ruby",
            "",
            "DEPENDENCIES",
            "  cocoapods (>=1.1.0)",
            "  cocoapods-keys",
            "",
            "BUNDLED WITH",
            "  1.14.6"
        );
    }

    @Override
    public Detectable create(@NotNull DetectableEnvironment detectableEnvironment) {
        return detectableFactory.createGemlockDetectable(detectableEnvironment);
    }

    @Override
    public void assertExtraction(@NotNull Extraction extraction) {
        Assertions.assertNotEquals(0, extraction.getCodeLocations().size(), "A code location should have been generated.");

        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.RUBYGEMS, extraction.getCodeLocations().get(0).getDependencyGraph());
        graphAssert.hasRootSize(2);
        graphAssert.hasRootDependency("cocoapods", "1.2.1");
        graphAssert.hasRootDependency("cocoapods-keys", "2.0.0");

        graphAssert.hasDependency("RubyInline", "3.12.4");
        graphAssert.hasParentChildRelationship("RubyInline", "3.12.4", "ZenTest", "4.11.1");

        graphAssert.hasParentChildRelationship("activesupport", "4.2.8", "thread_safe", "0.3.6");
        graphAssert.hasParentChildRelationship("cocoapods", "1.2.1", "activesupport", "4.2.8");
        graphAssert.hasParentChildRelationship("cocoapods-keys", "2.0.0", "osx_keychain", "1.0.1");
    }

}