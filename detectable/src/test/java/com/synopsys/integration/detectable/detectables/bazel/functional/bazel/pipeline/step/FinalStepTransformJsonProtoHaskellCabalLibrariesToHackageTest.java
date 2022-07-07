package com.synopsys.integration.detectable.detectables.bazel.functional.bazel.pipeline.step;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.synopsys.integration.bdio.graph.BasicDependencyGraph;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.step.FinalStepTransformJsonProtoHaskellCabalLibrariesToHackage;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.step.HaskellCabalLibraryJsonProtoParser;
import com.synopsys.integration.detectable.util.graph.GraphAssert;
import com.synopsys.integration.exception.IntegrationException;

public class FinalStepTransformJsonProtoHaskellCabalLibrariesToHackageTest {

    @Test
    public void testStep() throws IntegrationException, IOException {
        File jsonProtoFile = new File("src/test/resources/detectables/functional/bazel/jsonProtoForHaskellCabalLibraries.txt");
        String jsonProtoHaskellCabalLibrary = FileUtils.readFileToString(jsonProtoFile, StandardCharsets.UTF_8);
        FinalStepTransformJsonProtoHaskellCabalLibrariesToHackage step = new FinalStepTransformJsonProtoHaskellCabalLibrariesToHackage(
            new HaskellCabalLibraryJsonProtoParser(new Gson()),
            new ExternalIdFactory()
        );
        List<String> input = new ArrayList<>(1);
        input.add(jsonProtoHaskellCabalLibrary);

        List<Dependency> dependencies = step.finish(input);
        DependencyGraph dependencyGraph = new BasicDependencyGraph();
        for (Dependency dependency : dependencies) {
            dependencyGraph.addDirectDependency(dependency);
        }
        Forge hackageForge = new Forge("/", "hackage");
        GraphAssert graphAssert = new GraphAssert(hackageForge, dependencyGraph);
        graphAssert.hasRootSize(1);
        ExternalId expectedExternalId = new ExternalIdFactory().createNameVersionExternalId(hackageForge, "colour", "2.3.5");
        graphAssert.hasRootDependency(expectedExternalId);
    }
}
