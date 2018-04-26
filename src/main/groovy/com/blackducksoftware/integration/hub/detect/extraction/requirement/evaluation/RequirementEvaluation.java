package com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation;

public class Evaluation<V> {

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
