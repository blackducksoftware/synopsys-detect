package com.synopsys.integration.detectable.detectables.yarn.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.synopsys.integration.detectable.detectables.yarn.packagejson.NullSafePackageJson;
import com.synopsys.integration.detectable.detectables.yarn.packagejson.PackageJsonReader;

public class PackageJsonReaderTest {
    private static PackageJsonReader packageJsonReader;

    @BeforeAll
    static void setup() {
        Gson gson = new Gson();
        packageJsonReader = new PackageJsonReader(gson);
    }

    @Test
    void testSimpleRead() {
        NullSafePackageJson packageJson = packageJsonReader.read(getPackageJsonTextNewFormat());

        assertEquals("simple", packageJson.getName().orElse(null));
        assertEquals("1.0.0", packageJson.getVersion().orElse(null));
        assertEquals(2, packageJson.getDependencies().size());
    }

    @Test
    void testWorkspacesNewFormat() {
        List<String> workspaceDirPatterns = packageJsonReader.extractWorkspaceDirPatterns(getPackageJsonTextNewFormat());
        assertEquals(1, workspaceDirPatterns.size());
        assertEquals("mypkgs/*", workspaceDirPatterns.get(0));
    }

    @Test
    void testWorkspacesOldFormat() {
        List<String> workspaceDirPatterns = packageJsonReader.extractWorkspaceDirPatterns(getPackageJsonTextOldFormat());
        assertEquals(2, workspaceDirPatterns.size());
        assertTrue(workspaceDirPatterns.get(0).startsWith("workspace-"));
        assertTrue(workspaceDirPatterns.get(1).startsWith("workspace-"));
    }

    private String getPackageJsonTextNewFormat() {
        StringBuilder packageJsonText = new StringBuilder();
        packageJsonText.append("{\n");
        packageJsonText.append("  \"name\": \"simple\",\n");
        packageJsonText.append("  \"version\": \"1.0.0\",\n");
        packageJsonText.append("  \"workspaces\": {\n");
        packageJsonText.append("    \"packages\": [\n");
        packageJsonText.append("      \"mypkgs/*\"\n");
        packageJsonText.append("    ]\n");
        packageJsonText.append("  },\n");
        packageJsonText.append("  \"dependencies\": {\n");
        packageJsonText.append("    \"semver\": \"^7.3.4\",\n");
        packageJsonText.append("    \"workspace-b\": \"1.0.0\"\n");
        packageJsonText.append("  }");
        packageJsonText.append("}");
        return packageJsonText.toString();
    }

    private String getPackageJsonTextOldFormat() {
        StringBuilder packageJsonText = new StringBuilder();
        packageJsonText.append("{\n");
        packageJsonText.append("  \"private\": true,\n");
        packageJsonText.append("  \"workspaces\": {\n");
        packageJsonText.append("      \"packages\": [ \"workspace-a\", \"workspace-b\" ]\n");
        packageJsonText.append("  }\n");
        packageJsonText.append("}");
        return packageJsonText.toString();
    }
}
