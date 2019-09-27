package com.synopsys.integration.detectable.detectables.bazel.functional.bazel;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.detectable.executable.ExecutableOutput;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunner;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunnerException;
import com.synopsys.integration.detectable.detectables.bazel.BazelExtractor;
import com.synopsys.integration.detectable.detectables.bazel.model.BazelExternalId;
import com.synopsys.integration.detectable.detectables.bazel.model.BazelExternalIdExtractionFullRuleJsonProcessor;
import com.synopsys.integration.detectable.detectables.bazel.model.pipeline.PipelineJsonProcessor;
import com.synopsys.integration.detectable.detectables.bazel.parse.BazelCodeLocationBuilder;
import com.synopsys.integration.detectable.detectables.bazel.parse.BazelQueryXmlOutputParser;
import com.synopsys.integration.detectable.detectables.bazel.parse.XPathParser;

public class BazelExtractorTest {


    // TODO make this better

    @Test
    public void testDefault() throws ExecutableRunnerException, IOException {

        final String pipelineStepsPath = "src/test/resources/detectables/functional/bazel/pipeline_steps_scenario1.json";
        final File workspaceDir = new File(".");

        final ExecutableRunner executableRunner = Mockito.mock(ExecutableRunner.class);

        final BazelQueryXmlOutputParser parser = null;
        final ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        final BazelCodeLocationBuilder codeLocationBuilder = new BazelCodeLocationBuilder(externalIdFactory);

        final PipelineJsonProcessor pipelineJsonProcessor = new PipelineJsonProcessor(new GsonBuilder().setPrettyPrinting().create());

        final BazelExtractor bazelExtractor = new BazelExtractor(executableRunner, parser, codeLocationBuilder, pipelineJsonProcessor);
        final File bazelExe = new File("/usr/bin/bazel");
        final List<String> bazelArgs = new ArrayList<>();
        bazelArgs.add("query");
        bazelArgs.add("filter(\\\"@.*:jar\\\", deps(//:ProjectRunner))");
        final ExecutableOutput bazelCmdExecutableOutput = Mockito.mock(ExecutableOutput.class);
        Mockito.when(bazelCmdExecutableOutput.getReturnCode()).thenReturn(0);
        Mockito.when(bazelCmdExecutableOutput.getStandardOutput()).thenReturn("@org_apache_commons_commons_io//jar:jar\n@com_google_guava_guava//jar:jar");
        Mockito.when(executableRunner.execute(workspaceDir, bazelExe, bazelArgs)).thenReturn(bazelCmdExecutableOutput);
        final Extraction result = bazelExtractor.extract(bazelExe, workspaceDir, "//:ProjectRunner", pipelineStepsPath);

        assertEquals(1, result.getCodeLocations().size());
        final Set<Dependency> dependencies = result.getCodeLocations().get(0).getDependencyGraph().getRootDependencies();
        assertEquals(3, dependencies.size());
        for (final Dependency dep : dependencies) {
            System.out.printf("externalId: %s\n", dep.externalId);
        }
    }
}
