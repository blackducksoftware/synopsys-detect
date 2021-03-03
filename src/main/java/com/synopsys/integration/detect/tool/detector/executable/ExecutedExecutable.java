/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.tool.detector.executable;

import com.synopsys.integration.executable.Executable;
import com.synopsys.integration.executable.ExecutableOutput;

public class ExecutedExecutable {
    private final ExecutableOutput output;
    private final Executable executable;

    public ExecutedExecutable(final ExecutableOutput output, final Executable executable) {
        this.output = output;
        this.executable = executable;
    }

    public ExecutableOutput getOutput() {
        return output;
    }

    public Executable getExecutable() {
        return executable;
    }
}
