/**
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.cli;

import com.synopsys.integration.executable.ExecutableOutput;
import com.synopsys.integration.executable.ExecutableRunnerException;
import com.synopsys.integration.executable.ProcessBuilderRunner;
import com.synopsys.integration.log.IntLogger;

public class PolarisCliRunner {
    private ProcessBuilderRunner processBuilderRunner;

    public PolarisCliRunner(final IntLogger logger) {
        this.processBuilderRunner = new ProcessBuilderRunner(logger);
    }

    public ExecutableOutput execute(PolarisCliExecutable polarisCliExecutable) throws ExecutableRunnerException {
        return processBuilderRunner.execute(polarisCliExecutable);
    }
}
