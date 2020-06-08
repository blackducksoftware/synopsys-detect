package com.synopsys.integration.detectable.detectables.bazel.functional.bazel.pipeline.step;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.step.FinalStepJsonProtoHaskellCabalLibraries;
import com.synopsys.integration.exception.IntegrationException;

public class FinalStepJsonProtoHaskellCabalLibrariesTest {

    @Test
    public void test() throws IntegrationException, IOException {
        final File jsonProtoFile = new File("src/test/resources/detectables/functional/bazel/jsonProtoForHaskellCabalLibraries.txt");
        final String jsonProtoHaskellCabalLibrary = FileUtils.readFileToString(jsonProtoFile, StandardCharsets.UTF_8);
        final FinalStepJsonProtoHaskellCabalLibraries step = new FinalStepJsonProtoHaskellCabalLibraries(new Gson());
        final List<String> input = new ArrayList<>(1);
        input.add(jsonProtoHaskellCabalLibrary);
        final MutableDependencyGraph graph = step.finish(input);
        // TODO Finish me!
        assertEquals(99, graph.getRootDependencies().size());
    }
}
