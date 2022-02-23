package com.synopsys.integration.detectable.detectables.lerna.unit;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectables.lerna.LernaPackageDiscoverer;
import com.synopsys.integration.detectable.detectables.lerna.model.LernaPackage;
import com.synopsys.integration.executable.Executable;
import com.synopsys.integration.executable.ExecutableOutput;
import com.synopsys.integration.executable.ExecutableRunnerException;

class LernaPackageDiscovererTest {

    @Test
    void discoverLernaPackages() throws ExecutableRunnerException {
        File workingDirectory = new File("workingDir");
        ExecutableTarget lernaExecutable = ExecutableTarget.forCommand("lerna");

        DetectableExecutableRunner executableRunner = Mockito.mock(DetectableExecutableRunner.class);
        Mockito.when(executableRunner.execute(Mockito.any(Executable.class))).thenReturn(
            new ExecutableOutput(0, String.join(
                System.lineSeparator(),
                "[",
                "  {",
                "    \"name\": \"@lerna/packageA\",",
                "    \"version\": \"1.2.3\",",
                "    \"private\": false,",
                "    \"location\": \"/source/packages/packageA\"",
                "  },",
                "  {",
                "    \"name\": \"@lerna/packageB\",",
                "    \"version\": \"3.2.1\",",
                "    \"private\": true,",
                "    \"location\": \"/source/packages/packageB\"",
                "  },",
                "  {",
                "    \"name\": \"@lerna/packageC\",",
                "    \"version\": \"1.1.1\",",
                "    \"private\": true,",
                "    \"location\": \"/source/packages/packageC\"",
                "  }",
                "]"
            ),
                ""
            )
        );

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        LernaPackageDiscoverer lernaPackageDiscoverer = new LernaPackageDiscoverer(executableRunner, gson, Collections.singletonList("@lerna/packageC"), new LinkedList<>());

        List<LernaPackage> lernaPackages = lernaPackageDiscoverer.discoverLernaPackages(workingDirectory, lernaExecutable);

        Assertions.assertEquals(2, lernaPackages.size(), "Expected to find two Lerna packages.");

        LernaPackage lernaPackageA = lernaPackages.get(0);
        Assertions.assertEquals("@lerna/packageA", lernaPackageA.getName());
        Assertions.assertEquals("1.2.3", lernaPackageA.getVersion());
        Assertions.assertFalse(lernaPackageA.isPrivate());
        Assertions.assertEquals("/source/packages/packageA", lernaPackageA.getLocation());

        LernaPackage lernaPackageB = lernaPackages.get(1);
        Assertions.assertEquals("@lerna/packageB", lernaPackageB.getName());
        Assertions.assertEquals("3.2.1", lernaPackageB.getVersion());
        Assertions.assertTrue(lernaPackageB.isPrivate());
        Assertions.assertEquals("/source/packages/packageB", lernaPackageB.getLocation());
    }
}
