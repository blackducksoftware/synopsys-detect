package com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation;

import com.blackducksoftware.integration.hub.detect.extraction.bucket.Bucket;

public class Evaluation {

    public EvaluationResult result;

    public Bucket directoryBucket;
    public Bucket systemBucket;

    public Exception error;

    public String description;

    public enum EvaluationResult {
        Passed,
        Failed,
        Exception
    }

}
