/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable;

import java.io.File;

public class ExecutableTarget {
    private File fileTarget;
    private String stringTarget;

    private ExecutableTarget(final File fileTarget, final String stringTarget) {
        this.fileTarget = fileTarget;
        this.stringTarget = stringTarget;
    }

    public static ExecutableTarget forFile(File targetFile) { //For example "C:\bin\git.exe"
        return new ExecutableTarget(targetFile, null);
    }

    public static ExecutableTarget forCommand(String command) { //For example "git".
        return new ExecutableTarget(null, command);
    }

    public String toCommand() {
        if (stringTarget != null) {
            return stringTarget;
        }

        return fileTarget.getAbsolutePath();
    }
}
