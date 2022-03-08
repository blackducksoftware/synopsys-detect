package com.synopsys.integration.detectable.detectables.pear.functional;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.executable.resolver.PearResolver;
import com.synopsys.integration.detectable.detectable.util.EnumListFilter;
import com.synopsys.integration.detectable.detectables.pear.PearCliDetectableOptions;
import com.synopsys.integration.detectable.detectables.pear.PearDependencyType;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.functional.DetectableFunctionalTest;
import com.synopsys.integration.detectable.util.graph.NameVersionGraphAssert;
import com.synopsys.integration.executable.ExecutableOutput;

public class PearCliDetectableTest extends DetectableFunctionalTest {

    public PearCliDetectableTest() throws IOException {
        super("pear");
    }

    @Override
    protected void setup() throws IOException {
        addFile(
            Paths.get("package.xml"),
            "<?xml version=\"1.0\"?>",
            "<!DOCTYPE package SYSTEM \"http://pear.php.net/dtd/package-1.0\">",
            "<package xmlns=\"http://pear.php.net/dtd/package-2.0\">",
            "   <name>couchbase</name>",
            "   <version>",
            "       <release>3.0.1</release>",
            "       <api>3.0.0</api>",
            "   </version>",
            "   <dependencies>",
            "       <required>",
            "           <php>",
            "               <min>7.1.0</min>",
            "           </php>",
            "           <pearinstaller>",
            "               <min>1.10.1</min>",
            "           </pearinstaller>",
            "       </required>",
            "   </dependencies>",
            "</package>"
        );

        ExecutableOutput listOutput = createStandardOutput(
            "==================",
            "PHP    7.1.0",
            "PearInstaller  1.10.1"
        );
        addExecutableOutput(listOutput, new HashMap<>(), "pear", "list");

        ExecutableOutput packageDependenciesOutput = createStandardOutput(
            "==================",
            "Y  Package PHP",
            "Y  Package PearInstaller"
        );
        addExecutableOutput(packageDependenciesOutput, new HashMap<>(), "pear", "package-dependencies", "package.xml");
    }

    @NotNull
    @Override
    public Detectable create(@NotNull DetectableEnvironment detectableEnvironment) {
        class LocalPearResolver implements PearResolver {
            @Override
            public ExecutableTarget resolvePear() {
                return ExecutableTarget.forCommand("pear");
            }
        }
        return detectableFactory.createPearCliDetectable(
            detectableEnvironment,
            new PearCliDetectableOptions(EnumListFilter.fromExcluded(PearDependencyType.OPTIONAL)),
            new LocalPearResolver()
        );
    }

    @Override
    public void assertExtraction(@NotNull Extraction extraction) {
        Assertions.assertNotEquals(0, extraction.getCodeLocations().size(), "A code location should have been generated.");

        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.PEAR, extraction.getCodeLocations().get(0).getDependencyGraph());
        graphAssert.hasRootSize(2);
        graphAssert.hasRootDependency("PHP", "7.1.0");
        graphAssert.hasRootDependency("PearInstaller", "1.10.1");

    }
}
