package com.synopsys.integration.detectable.detectables.bazel.pipeline;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.step.BazelCommandExecutor;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.step.BazelVariableSubstitutor;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.step.FinalStep;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.step.FinalStepColonSeparatedGavs;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.step.FinalStepJsonProtoHaskellCabalLibraries;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.step.HaskellCabalLibraryJsonProtoParser;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.step.IntermediateStep;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.step.IntermediateStepExecuteBazelOnEach;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.step.IntermediateStepFilter;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.step.IntermediateStepParseEachXml;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.step.IntermediateStepReplaceInEach;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.step.IntermediateStepSplitEach;

public class PipelineBuilder {
    private final ExternalIdFactory externalIdFactory;
    private final BazelCommandExecutor bazelCommandExecutor;
    private final BazelVariableSubstitutor bazelVariableSubstitutor;
    private final HaskellCabalLibraryJsonProtoParser haskellCabalLibraryJsonProtoParser;
    private final List<IntermediateStep> intermediateSteps = new ArrayList<>();
    private FinalStep finalStep;

    public PipelineBuilder(ExternalIdFactory externalIdFactory, BazelCommandExecutor bazelCommandExecutor, BazelVariableSubstitutor bazelVariableSubstitutor,
        HaskellCabalLibraryJsonProtoParser haskellCabalLibraryJsonProtoParser) {
        this.externalIdFactory = externalIdFactory;
        this.bazelCommandExecutor = bazelCommandExecutor;
        this.bazelVariableSubstitutor = bazelVariableSubstitutor;
        this.haskellCabalLibraryJsonProtoParser = haskellCabalLibraryJsonProtoParser;
    }

    public PipelineBuilder addIntermediateStep(IntermediateStep intermediateStep) {
        intermediateSteps.add(intermediateStep);
        return this;
    }

    public PipelineBuilder setFinalStep(FinalStep finalStep) {
        this.finalStep = finalStep;
        return this;
    }

    public Pipeline build() {
        if (finalStep == null) {
            throw new UnsupportedOperationException("A final step is required");
        }
        return new Pipeline(intermediateSteps, finalStep);
    }

    //TODO: Add helper step methods.
    public PipelineBuilder replaceInEachLine(String from, String to) {
        return addIntermediateStep(new IntermediateStepReplaceInEach(from, to));
    }

    public PipelineBuilder splitEachLine(String splitOn) {
        return addIntermediateStep(new IntermediateStepSplitEach(splitOn));
    }

    public PipelineBuilder executeBazelOnEachLine(List<String> bazelArguments, boolean inputIsExpected) {
        return addIntermediateStep(new IntermediateStepExecuteBazelOnEach(bazelCommandExecutor, bazelVariableSubstitutor, bazelArguments, inputIsExpected));
    }

    public PipelineBuilder parseValueFromEachXmlLine(String xPathToElement, String targetAttributeName) {
        return addIntermediateStep(new IntermediateStepParseEachXml(xPathToElement, targetAttributeName));
    }

    public PipelineBuilder filterLines(String regex) {
        return addIntermediateStep(new IntermediateStepFilter(regex));
    }

    // These can only be the final step in the pipeline
    public PipelineBuilder generateMavenDependenciesFromLines() {
        return setFinalStep(new FinalStepColonSeparatedGavs(externalIdFactory));
    }

    public PipelineBuilder generateHackageDependenciesFromLines() {
        return setFinalStep(new FinalStepJsonProtoHaskellCabalLibraries(haskellCabalLibraryJsonProtoParser, externalIdFactory));
    }
}
