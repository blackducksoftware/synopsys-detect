package com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation;

public class RequirementEvaluation<V> {

    public RequirementEvaluation(final EvaluationResult result, final V value) {
        this.result = result;
        this.value = value;
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

}
