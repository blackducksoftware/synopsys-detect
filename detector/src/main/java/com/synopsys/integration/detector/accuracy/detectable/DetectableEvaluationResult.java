package com.synopsys.integration.detector.accuracy.detectable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.detectable.detectable.explanation.Explanation;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;
import com.synopsys.integration.detector.rule.DetectableDefinition;

// TODO(Detector):Might want a builder
// TODO(Detector): Should this be DetectableEvaluation? But that exists. But Detector is just DetectorEvaluation. Inconsistent names, revisit.
public class DetectableEvaluationResult {
    @NotNull
    private final DetectableEvaluationResultType resultType;
    @NotNull
    private final DetectableDefinition detectableDefinition;
    @NotNull
    private final DetectableResult applicableResult;
    @Nullable
    private final DetectableResult extractableResult;
    @Nullable
    private final ExtractionEnvironment extractionEnvironment;
    @Nullable
    private final Extraction extraction;

    public DetectableEvaluationResult(
        @NotNull DetectableEvaluationResultType resultType,
        @NotNull DetectableDefinition detectableDefinition,
        @NotNull DetectableResult applicableResult,
        @Nullable DetectableResult extractableResult,
        @Nullable ExtractionEnvironment extractionEnvironment,
        @Nullable Extraction extraction
    ) {
        this.resultType = resultType;
        this.detectableDefinition = detectableDefinition;
        this.applicableResult = applicableResult;
        this.extractableResult = extractableResult;
        this.extractionEnvironment = extractionEnvironment;
        this.extraction = extraction;
    }

    public @NotNull DetectableDefinition getDetectableDefinition() {
        return detectableDefinition;
    }

    public List<Explanation> getExplanations() {
        List<Explanation> explanations = new ArrayList<>();
        if (applicableResult != null) {
            explanations.addAll(applicableResult.getExplanation());
        }
        if (extractableResult != null) {
            explanations.addAll(extractableResult.getExplanation());
        }
        return explanations;
    }

    public List<File> getRelevantFiles() {
        List<File> explanations = new ArrayList<>();
        if (applicableResult != null) {
            explanations.addAll(applicableResult.getRelevantFiles());
        }
        if (extractableResult != null) {
            explanations.addAll(extractableResult.getRelevantFiles());
        }
        return explanations;
    }

    public Optional<ExtractionEnvironment> getExtractionEnvironment() {
        return Optional.ofNullable(extractionEnvironment);
    }

    @NotNull
    public DetectableResult getApplicable() {
        return applicableResult;
    }

    @Nullable
    public DetectableResult getExtractable() {
        return extractableResult;
    }

    public List<Explanation> getAllExplanations() {
        List<Explanation> explanations = new ArrayList<>();
        if (applicableResult != null) {
            explanations.addAll(applicableResult.getExplanation());
        }
        if (extractableResult != null) {
            explanations.addAll(extractableResult.getExplanation());
        }
        return explanations;
    }

    private enum DetectableEvaluationResultType {
        NOT_APPLICABLE,
        NOT_EXTRACTABLE,
        EXTRACTED
    }

    public static DetectableEvaluationResult notApplicable(DetectableDefinition detectableDefinition, DetectableResult applicable) {
        return new DetectableEvaluationResult(DetectableEvaluationResultType.NOT_APPLICABLE, detectableDefinition, applicable, null, null, null);
    }

    public static DetectableEvaluationResult notExtractable(DetectableDefinition detectableDefinition, DetectableResult applicable, DetectableResult extractable) {
        return new DetectableEvaluationResult(DetectableEvaluationResultType.NOT_EXTRACTABLE, detectableDefinition, applicable, extractable, null, null);

    }

    public static DetectableEvaluationResult extracted(
        DetectableDefinition detectableDefinition,
        DetectableResult applicable,
        DetectableResult extractable,
        ExtractionEnvironment extractionEnvironment,
        Extraction extraction
    ) {
        return new DetectableEvaluationResult(DetectableEvaluationResultType.EXTRACTED, detectableDefinition, applicable, extractable, extractionEnvironment, extraction);

    }

    public boolean wasExtractionSuccessful() {
        return resultType == DetectableEvaluationResultType.EXTRACTED && extraction != null && extraction.isSuccess();
    }

    public @Nullable Extraction getExtraction() {
        return extraction;
    }

}
