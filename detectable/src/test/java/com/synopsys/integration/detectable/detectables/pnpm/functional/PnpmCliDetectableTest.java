package com.synopsys.integration.detectable.detectables.pnpm.functional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.resolver.PnpmResolver;
import com.synopsys.integration.detectable.detectables.pnpm.cli.PnpmCliExtractorOptions;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.functional.DetectableFunctionalTest;
import com.synopsys.integration.detectable.util.graph.NameVersionGraphAssert;
import com.synopsys.integration.executable.ExecutableOutput;

public class PnpmCliDetectableTest extends DetectableFunctionalTest {
    public PnpmCliDetectableTest() throws IOException {
        super("pnpmCli");
    }

    @Override
    protected void setup() throws IOException {
        addFile(Paths.get("package.json"));
        addFile(Paths.get("node_modules"));

        ExecutableOutput pnpmListOutput = createStandardOutput(
            "[",
            "  {",
            "    \"name\": \"tech-doc-hugo\",",
            "    \"version\": \"0.0.1\",",
            "    \"path\": \"/Users/crowley/Desktop/test-projects/pnpm/documentation\",",
            "    \"dependencies\": {",
            "      \"material-design-icons\": {",
            "        \"from\": \"material-design-icons\",",
            "        \"version\": \"3.0.1\",",
            "        \"resolved\": \"https://registry.npmjs.org/material-design-icons/-/material-design-icons-3.0.1.tgz\"",
            "      }",
            "    },",
            "    \"devDependencies\": {",
            "      \"autoprefixer\": {",
            "        \"from\": \"autoprefixer\",",
            "        \"version\": \"9.8.6\",",
            "        \"resolved\": \"https://registry.npmjs.org/autoprefixer/-/autoprefixer-9.8.6.tgz\",",
            "        \"dependencies\": {",
            "          \"caniuse-lite\": {",
            "            \"from\": \"caniuse-lite\",",
            "            \"version\": \"1.0.30001230\",",
            "            \"resolved\": \"https://registry.npmjs.org/caniuse-lite/-/caniuse-lite-1.0.30001230.tgz\"",
            "          },",
            "          \"colorette\": {",
            "            \"from\": \"colorette\",",
            "            \"version\": \"1.2.2\",",
            "            \"resolved\": \"https://registry.npmjs.org/colorette/-/colorette-1.2.2.tgz\"",
            "          }",
            "         }",
            "       }",
            "    }",
            "  }",
            "]"
        );
        addExecutableOutput(pnpmListOutput, new File("pnpm").getAbsolutePath(), "ls", "--json");
    }

    @Override
    public @NotNull Detectable create(@NotNull DetectableEnvironment detectableEnvironment) {
        class PnpmResolverTest implements PnpmResolver {

            @Override
            public ExecutableTarget resolvePnpm(DetectableEnvironment environment) throws DetectableException {
                return ExecutableTarget.forFile(new File("pnpm"));
            }
        }
        return detectableFactory.createPnpmCliDetectable(detectableEnvironment, new PnpmResolverTest(), new PnpmCliExtractorOptions(true, null));
    }

    @Override
    public void assertExtraction(@NotNull Extraction extraction) {
        Assertions.assertEquals(1, extraction.getCodeLocations().size(), "A code location should have been generated.");

        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.NPMJS, extraction.getCodeLocations().get(0).getDependencyGraph());
        graphAssert.hasRootSize(2);
        graphAssert.hasRootDependency("material-design-icons", "3.0.1");
        graphAssert.hasRootDependency("autoprefixer", "9.8.6");
        graphAssert.hasParentChildRelationship("autoprefixer", "9.8.6", "caniuse-lite", "1.0.30001230");
        graphAssert.hasParentChildRelationship("autoprefixer", "9.8.6", "colorette", "1.2.2");
    }
}
