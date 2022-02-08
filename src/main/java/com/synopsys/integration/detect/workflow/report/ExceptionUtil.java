package com.synopsys.integration.detect.workflow.report;

public class ExceptionUtil {
    public static String oneSentenceDescription(Exception exception) {
        if (exception.getMessage() == null) {
            return exception.getClass().getSimpleName() + ": Null Pointer Exception";
        } else {
            if (exception.getMessage().contains("\n")) {
                return exception.getClass().getSimpleName() + ": " + exception.getMessage().split("\r?\n")[0];
            } else {
                return exception.getClass().getSimpleName() + ": " + exception.getMessage();
            }

        }
    }
}
