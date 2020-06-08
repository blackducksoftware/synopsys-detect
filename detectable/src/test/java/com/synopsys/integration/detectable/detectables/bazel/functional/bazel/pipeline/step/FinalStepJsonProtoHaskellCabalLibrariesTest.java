package com.synopsys.integration.detectable.detectables.bazel.functional.bazel.pipeline.step;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.step.FinalStepJsonProtoHaskellCabalLibraries;
import com.synopsys.integration.exception.IntegrationException;

public class FinalStepJsonProtoHaskellCabalLibrariesTest {

    @Test
    public void testStep() throws IntegrationException, IOException {
        final File jsonProtoFile = new File("src/test/resources/detectables/functional/bazel/jsonProtoForHaskellCabalLibraries.txt");
        final String jsonProtoHaskellCabalLibrary = FileUtils.readFileToString(jsonProtoFile, StandardCharsets.UTF_8);
        final FinalStepJsonProtoHaskellCabalLibraries step = new FinalStepJsonProtoHaskellCabalLibraries();
        final List<String> input = new ArrayList<>(1);
        input.add(jsonProtoHaskellCabalLibrary);
        final MutableDependencyGraph graph = step.finish(input);
        assertEquals(5, graph.getRootDependencies().size());
        boolean foundTargetComp = false;
        for (final Dependency dep : graph.getRootDependencies()) {
            if ("colour".equals(dep.getExternalId().getName())) {
                foundTargetComp = true;
                break;
            }
        }
        assertTrue(foundTargetComp);
    }

    @Test
    public void testDependencyGeneration() {
        final FinalStepJsonProtoHaskellCabalLibraries step = new FinalStepJsonProtoHaskellCabalLibraries();
        final Dependency dep = step.haskageCompNameVersionToDependency("testComp", "testVersion");
        System.out.printf("dep externalId: %s\n", dep.getExternalId());
        assertEquals("hackage", dep.getExternalId().getForge().getName());
        assertEquals("/", dep.getExternalId().getForge().getSeparator());
        assertEquals("testComp", dep.getExternalId().getName());
        assertEquals("testVersion", dep.getExternalId().getVersion());
        assertTrue(StringUtils.isBlank(dep.getExternalId().getGroup()));
        assertTrue(StringUtils.isBlank(dep.getExternalId().getArchitecture()));
    }
}
