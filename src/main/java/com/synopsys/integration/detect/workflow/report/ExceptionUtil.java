/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
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
