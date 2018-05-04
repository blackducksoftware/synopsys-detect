package com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation;

public class RequirementEvaluation<V> {

    public RequirementEvaluation(final EvaluationResult result, final V value, final String description) {
        this.result = result;
        this.value = value;
        this.description = description;
    }

    public RequirementEvaluation(final EvaluationResult result, final Exception e) {
        this.result = result;
        this.error = e;
    }

    public EvaluationResult result;

    public Exception error;

    public String description;

    public V value;

    public enum EvaluationResult {
        Passed,
        Failed,
        Exception
    }

    public static <T> RequirementEvaluation<T> failed(final T value, final String description) {
        return new RequirementEvaluation<>(EvaluationResult.Failed, value, description);
    }

    public static <T> RequirementEvaluation<T> passed(final T value) {
        return new RequirementEvaluation<>(EvaluationResult.Passed, value, "");
    }

    public static <T> RequirementEvaluation<T> error(final Exception e) {
        return new RequirementEvaluation<>(EvaluationResult.Exception, e);
    }

}
