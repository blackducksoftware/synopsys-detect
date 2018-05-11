package com.blackducksoftware.integration.hub.detect.extraction;

public class Extractable {

    public ExtractableResult result;
    public enum ExtractableResult {
        EXTRACTABLE,
        CAN_NOT_EXTRACT,
        EXCEPTION
    }
    public String description = "";
    public Exception error;

    public Extractable(final ExtractableResult applied, final String description) {
        this.result = applied;
    }

    public static Extractable canNotExtract(final String reason){
        return new Extractable(ExtractableResult.CAN_NOT_EXTRACT, reason);
    }

    public static Extractable canExtract(){
        return new Extractable(ExtractableResult.EXTRACTABLE, null);
    }

    public static Extractable exception(final Exception e) {
        return new Extractable(ExtractableResult.EXCEPTION, null);
    }
}
