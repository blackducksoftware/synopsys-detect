package com.synopsys.integration.detectable.detectables.bazel.pipeline;

import java.util.Arrays;
import java.util.EnumMap;

import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.bazel.WorkspaceRule;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.step.BazelCommandExecutor;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.step.BazelVariableSubstitutor;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.step.HaskellCabalLibraryJsonProtoParser;
import com.synopsys.integration.exception.IntegrationException;

public class Pipelines {
    private static final String CQUERY_OPTIONS_PLACEHOLDER = "${detect.bazel.cquery.options}";
    private static final String CQUERY_COMMAND = "cquery";
    private static final String OUTPUT_FLAG = "--output";
    private final EnumMap<WorkspaceRule, Pipeline> availablePipelines = new EnumMap<>(WorkspaceRule.class);

    public Pipelines(BazelCommandExecutor bazelCommandExecutor, BazelVariableSubstitutor bazelVariableSubstitutor,
        ExternalIdFactory externalIdFactory, HaskellCabalLibraryJsonProtoParser haskellCabalLibraryJsonProtoParser) {
        Pipeline mavenJarPipeline = (new PipelineBuilder(externalIdFactory, bazelCommandExecutor, bazelVariableSubstitutor, haskellCabalLibraryJsonProtoParser))
            .executeBazelOnEachLine(Arrays.asList(CQUERY_COMMAND, CQUERY_OPTIONS_PLACEHOLDER, "filter('@.*:jar', deps(${detect.bazel.target}))"), false)
            // The trailing parens may contain a hex number, or "null"; the pattern below handles either
            .replaceInEachLine(" \\([0-9a-z]+\\)", "")
            .splitEachLine("\\s+")
            .replaceInEachLine("^@", "")
            .replaceInEachLine("//.*", "")
            .replaceInEachLine("^", "//external:")
            .executeBazelOnEachLine(Arrays.asList("query", "kind(maven_jar, ${input.item})", OUTPUT_FLAG, "xml"), true)
            .parseValueFromEachXmlLine("/query/rule[@class='maven_jar']/string[@name='artifact']", "value")
            .generateMavenDependenciesFromLines()
            .build();
        availablePipelines.put(WorkspaceRule.MAVEN_JAR, mavenJarPipeline);

        Pipeline mavenInstallPipeline = (new PipelineBuilder(externalIdFactory, bazelCommandExecutor, bazelVariableSubstitutor, haskellCabalLibraryJsonProtoParser))
            .executeBazelOnEachLine(Arrays.asList(CQUERY_COMMAND, "--noimplicit_deps", CQUERY_OPTIONS_PLACEHOLDER, "kind(j.*import, deps(${detect.bazel.target}))", OUTPUT_FLAG, "build"), false)
            .splitEachLine("\r?\n")
            .filterLines(".*maven_coordinates=.*")
            .replaceInEachLine(".*\"maven_coordinates=", "")
            .replaceInEachLine("\".*", "")
            .generateMavenDependenciesFromLines()
            .build();
        availablePipelines.put(WorkspaceRule.MAVEN_INSTALL, mavenInstallPipeline);

        Pipeline haskellCabalLibraryPipeline = (new PipelineBuilder(externalIdFactory, bazelCommandExecutor, bazelVariableSubstitutor, haskellCabalLibraryJsonProtoParser))
            .executeBazelOnEachLine(Arrays.asList(CQUERY_COMMAND, "--noimplicit_deps", CQUERY_OPTIONS_PLACEHOLDER, "kind(haskell_cabal_library, deps(${detect.bazel.target}))", OUTPUT_FLAG, "jsonproto"), false)
            .generateHackageDependenciesFromLines()
            .build();
        availablePipelines.put(WorkspaceRule.HASKELL_CABAL_LIBRARY, haskellCabalLibraryPipeline);
    }

    public Pipeline get(WorkspaceRule bazelDependencyType) throws IntegrationException {
        if (!availablePipelines.containsKey(bazelDependencyType)) {
            throw new IntegrationException(String.format("No pipeline found for dependency type %s", bazelDependencyType.getName()));
        }
        return availablePipelines.get(bazelDependencyType);
    }
}
