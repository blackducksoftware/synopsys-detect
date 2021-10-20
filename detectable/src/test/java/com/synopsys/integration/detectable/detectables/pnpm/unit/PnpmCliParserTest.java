package com.synopsys.integration.detectable.detectables.pnpm.unit;

import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.npm.lockfile.model.NpmParseResult;
import com.synopsys.integration.detectable.detectables.pnpm.cli.PnpmCliParser;
import com.synopsys.integration.detectable.util.graph.NameVersionGraphAssert;

public class PnpmCliParserTest {
    private PnpmCliParser parser = new PnpmCliParser(new ExternalIdFactory());

    private String pnpmListOutput = String.join("\n", Arrays.asList(
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
        "          }",
        "         }",
        "       }",
        "    }",
        "  }",
        "]"
    ));

    @Test
    public void testGenerateCodeLocation() {
        NpmParseResult result = parser.generateCodeLocation(pnpmListOutput, true);
        Assertions.assertEquals("tech-doc-hugo", result.getProjectName());
        Assertions.assertEquals("0.0.1", result.getProjectVersion());

        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.NPMJS, result.getCodeLocation().getDependencyGraph());
        graphAssert.hasRootSize(2);
        graphAssert.hasRootDependency("material-design-icons", "3.0.1");
        graphAssert.hasRootDependency("autoprefixer", "9.8.6");
        graphAssert.hasParentChildRelationship("autoprefixer", "9.8.6", "caniuse-lite", "1.0.30001230");
    }

    @Test
    public void testExcludeDevDependencies() {
        NpmParseResult result = parser.generateCodeLocation(pnpmListOutput, false);
        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.NPMJS, result.getCodeLocation().getDependencyGraph());
        graphAssert.hasRootSize(1);
        graphAssert.hasRootDependency("material-design-icons", "3.0.1");
        graphAssert.hasNoDependency("autoprefixer", "9.8.6");
    }
}
