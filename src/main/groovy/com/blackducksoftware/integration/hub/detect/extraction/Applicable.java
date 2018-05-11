package com.blackducksoftware.integration.hub.detect.extraction;

public class Applicable {

    public ApplicableResult result;
    public enum ApplicableResult {
        APPLIES,
        DOES_NOT_APPLY,
        EXCEPTION
    }
    public String description = "";
    public Exception error;

    public Applicable(final ApplicableResult applied, final String description) {
        this.result = applied;
    }

    public static Applicable doesNotApply(final String reason){
        return new Applicable(ApplicableResult.DOES_NOT_APPLY, reason);
    }

    public static Applicable doesApply(){
        return new Applicable(ApplicableResult.APPLIES, null);
    }

    public static Applicable exception(final Exception e) {
        return new Applicable(ApplicableResult.EXCEPTION, null);
    }
}
