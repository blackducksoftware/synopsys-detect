package com.synopsys.integration.detect.workflow.report;

public class ExceptionUtil {
    public static String oneSentenceDescription(Exception exception) {
        if (exception.getMessage() == null){
            return exception.getClass().getSimpleName() + ": Null Pointer Exception";
        } else {
            return exception.getClass().getSimpleName() + ": " + exception.getMessage();
        }
    }
}
