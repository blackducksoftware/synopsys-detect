/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.bazel.pipeline;

import java.util.Arrays;
import java.util.EnumMap;

import com.google.gson.Gson;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.bazel.WorkspaceRule;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.step.BazelCommandExecutor;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.step.BazelVariableSubstitutor;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.step.FinalStepColonSeparatedGavs;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.step.FinalStepJsonProtoHaskellCabalLibraries;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.step.HaskellCabalLibraryJsonProtoParser;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.step.IntermediateStepExecuteBazelOnEach;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.step.IntermediateStepFilter;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.step.IntermediateStepParseEachXml;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.step.IntermediateStepReplaceInEach;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.step.IntermediateStepSplitEach;
import com.synopsys.integration.exception.IntegrationException;

public class Pipelines {
    private static final String CQUERY_OPTIONS_PLACEHOLDER = "${detect.bazel.cquery.options}";
    private static final String CQUERY_COMMAND = "cquery";
    private static final String OUTPUT_FLAG = "--output";
    private final EnumMap<WorkspaceRule, Pipeline> availablePipelines = new EnumMap<>(WorkspaceRule.class);
    private final Gson gson = new Gson();

    public Pipelines(BazelCommandExecutor bazelCommandExecutor, BazelVariableSubstitutor bazelVariableSubstitutor,
        ExternalIdFactory externalIdFactory) {
        Pipeline mavenJarPipeline = (new PipelineBuilder())
                                        .addIntermediateStep(new IntermediateStepExecuteBazelOnEach(bazelCommandExecutor, bazelVariableSubstitutor,
                                            Arrays.asList(CQUERY_COMMAND, CQUERY_OPTIONS_PLACEHOLDER, "filter('@.*:jar', deps(${detect.bazel.target}))"), false))
                                        // The trailing parens may contain a hex number, or "null"; the pattern below handles either
                                        .addIntermediateStep(new IntermediateStepReplaceInEach(" \\([0-9a-z]+\\)", ""))
                                        .addIntermediateStep(new IntermediateStepSplitEach("\\s+"))
                                        .addIntermediateStep(new IntermediateStepReplaceInEach("^@", ""))
                                        .addIntermediateStep(new IntermediateStepReplaceInEach("//.*", ""))
                                        .addIntermediateStep(new IntermediateStepReplaceInEach("^", "//external:"))
                                        .addIntermediateStep(new IntermediateStepExecuteBazelOnEach(bazelCommandExecutor, bazelVariableSubstitutor, Arrays.asList("query", "kind(maven_jar, ${input.item})", OUTPUT_FLAG, "xml"), true))
                                        .addIntermediateStep(new IntermediateStepParseEachXml("/query/rule[@class='maven_jar']/string[@name='artifact']", "value"))
                                        .setFinalStep(new FinalStepColonSeparatedGavs(externalIdFactory))
                                        .build();
        availablePipelines.put(WorkspaceRule.MAVEN_JAR, mavenJarPipeline);

        Pipeline mavenInstallPipeline = (new PipelineBuilder())
                                            .addIntermediateStep(new IntermediateStepExecuteBazelOnEach(bazelCommandExecutor, bazelVariableSubstitutor,
                                                Arrays.asList(CQUERY_COMMAND, "--noimplicit_deps", CQUERY_OPTIONS_PLACEHOLDER, "kind(j.*import, deps(${detect.bazel.target}))", OUTPUT_FLAG, "build"), false))
                                            .addIntermediateStep(new IntermediateStepSplitEach("\r?\n"))
                                            .addIntermediateStep(new IntermediateStepFilter(".*maven_coordinates=.*"))
                                            .addIntermediateStep(new IntermediateStepReplaceInEach(".*\"maven_coordinates=", ""))
                                            .addIntermediateStep(new IntermediateStepReplaceInEach("\".*", ""))
                                            .setFinalStep(new FinalStepColonSeparatedGavs(externalIdFactory))
                                            .build();
        availablePipelines.put(WorkspaceRule.MAVEN_INSTALL, mavenInstallPipeline);

        HaskellCabalLibraryJsonProtoParser haskellCabalLibraryJsonProtoParser = new HaskellCabalLibraryJsonProtoParser(gson);
        Pipeline haskellCabalLibraryPipeline = (new PipelineBuilder())
                                                   .addIntermediateStep(new IntermediateStepExecuteBazelOnEach(bazelCommandExecutor, bazelVariableSubstitutor,
                                                       Arrays.asList(CQUERY_COMMAND, "--noimplicit_deps", CQUERY_OPTIONS_PLACEHOLDER, "kind(haskell_cabal_library, deps(${detect.bazel.target}))", OUTPUT_FLAG, "jsonproto"), false))
                                                   .setFinalStep(new FinalStepJsonProtoHaskellCabalLibraries(haskellCabalLibraryJsonProtoParser, externalIdFactory))
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
