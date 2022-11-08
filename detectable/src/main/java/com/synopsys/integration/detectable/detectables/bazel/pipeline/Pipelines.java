package com.synopsys.integration.detectable.detectables.bazel.pipeline;

import java.util.Arrays;
import java.util.EnumMap;

import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectables.bazel.WorkspaceRule;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.step.BazelCommandExecutor;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.step.BazelVariableSubstitutor;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.step.HaskellCabalLibraryJsonProtoParser;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.xpathquery.HttpArchiveXpath;

public class Pipelines {
    private static final String CQUERY_OPTIONS_PLACEHOLDER = "${detect.bazel.cquery.options}";
    private static final String QUERY_COMMAND = "query";
    private static final String CQUERY_COMMAND = "cquery";
    private static final String OUTPUT_FLAG = "--output";
    private final EnumMap<WorkspaceRule, Pipeline> availablePipelines = new EnumMap<>(WorkspaceRule.class);

    public Pipelines(
        BazelCommandExecutor bazelCommandExecutor,
        BazelVariableSubstitutor bazelVariableSubstitutor,
        ExternalIdFactory externalIdFactory,
        HaskellCabalLibraryJsonProtoParser haskellCabalLibraryJsonProtoParser
    ) {
        Pipeline mavenJarPipeline = (new PipelineBuilder(externalIdFactory, bazelCommandExecutor, bazelVariableSubstitutor, haskellCabalLibraryJsonProtoParser))
            .executeBazelOnEachLine(Arrays.asList(CQUERY_COMMAND, CQUERY_OPTIONS_PLACEHOLDER, "filter('@.*:jar', deps(${detect.bazel.target}))"), false)
            // The trailing parens may contain a hex number, or "null"; the pattern below handles either
            .parseReplaceInEachLine(" \\([0-9a-z]+\\)", "")
            .parseSplitEachLine("\\s+")
            .parseReplaceInEachLine("^@", "")
            .parseReplaceInEachLine("//.*", "")
            .parseReplaceInEachLine("^", "//external:")
            .executeBazelOnEachLine(Arrays.asList(QUERY_COMMAND, "kind(maven_jar, ${input.item})", OUTPUT_FLAG, "xml"), true)
            .parseValuesFromXml("/query/rule[@class='maven_jar']/string[@name='artifact']", "value")
            .transformToMavenDependencies()
            .build();
        availablePipelines.put(WorkspaceRule.MAVEN_JAR, mavenJarPipeline);

        Pipeline mavenInstallPipeline = (new PipelineBuilder(externalIdFactory, bazelCommandExecutor, bazelVariableSubstitutor, haskellCabalLibraryJsonProtoParser))
            .executeBazelOnEachLine(Arrays.asList(
                CQUERY_COMMAND,
                "--noimplicit_deps",
                CQUERY_OPTIONS_PLACEHOLDER,
                "kind(j.*import, deps(${detect.bazel.target}))",
                OUTPUT_FLAG,
                "build"
            ), false)
            .parseSplitEachLine("\r?\n")
            .parseFilterLines(".*maven_coordinates=.*")
            .parseReplaceInEachLine(".*\"maven_coordinates=", "")
            .parseReplaceInEachLine("\".*", "")
            .transformToMavenDependencies()
            .build();
        availablePipelines.put(WorkspaceRule.MAVEN_INSTALL, mavenInstallPipeline);

        Pipeline haskellCabalLibraryPipeline = (new PipelineBuilder(externalIdFactory, bazelCommandExecutor, bazelVariableSubstitutor, haskellCabalLibraryJsonProtoParser))
            .executeBazelOnEachLine(Arrays.asList(
                CQUERY_COMMAND,
                "--noimplicit_deps",
                CQUERY_OPTIONS_PLACEHOLDER,
                "kind(haskell_cabal_library, deps(${detect.bazel.target}))",
                OUTPUT_FLAG,
                "jsonproto"
            ), false)
            .transformToHackageDependencies()
            .build();
        availablePipelines.put(WorkspaceRule.HASKELL_CABAL_LIBRARY, haskellCabalLibraryPipeline);

        Pipeline httpArchiveGithubUrlPipeline = (new PipelineBuilder(externalIdFactory, bazelCommandExecutor, bazelVariableSubstitutor, haskellCabalLibraryJsonProtoParser))
            .executeBazelOnEachLine(Arrays.asList(QUERY_COMMAND, "kind(.*library, deps(${detect.bazel.target}))"), false)
            .parseSplitEachLine("\r?\n")
            .parseFilterLines("^@.*//.*$")
            .parseReplaceInEachLine("^@", "")
            .parseReplaceInEachLine("//.*", "")
            .deDupLines()
            .parseReplaceInEachLine("^", "//external:")
            .executeBazelOnEachLine(Arrays.asList(QUERY_COMMAND, "kind(.*, ${input.item})", OUTPUT_FLAG, "xml"), true)
            .parseValuesFromXml(HttpArchiveXpath.QUERY, "value")
            .transformGithubUrl()
            .build();
        availablePipelines.put(WorkspaceRule.HTTP_ARCHIVE, httpArchiveGithubUrlPipeline);
    }

    public Pipeline get(WorkspaceRule bazelDependencyType) throws DetectableException {
        if (!availablePipelines.containsKey(bazelDependencyType)) {
            throw new DetectableException(String.format("No pipeline found for dependency type %s", bazelDependencyType.getName()));
        }
        return availablePipelines.get(bazelDependencyType);
    }
}
