/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.lifecycle.exit;

import com.synopsys.integration.util.Stringable;

public class ExitOptions extends Stringable {
    private final long startTime;
    private final boolean logResults;
    private final boolean forceSuccessExit;
    private final boolean shouldExit;

    public ExitOptions(long startTime, boolean logResults, boolean forceSuccessExit, boolean shouldExit) {
        this.startTime = startTime;
        this.logResults = logResults;
        this.forceSuccessExit = forceSuccessExit;
        this.shouldExit = shouldExit;
    }

    public long getStartTime() {
        return startTime;
    }

    public boolean shouldLogResults() {
        return logResults;
    }

    public boolean shouldForceSuccessExit() {
        return forceSuccessExit;
    }

    public boolean shouldExit() {
        return shouldExit;
    }
}
