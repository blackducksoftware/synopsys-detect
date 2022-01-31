package com.synopsys.integration.detect.lifecycle.exit;

import com.synopsys.integration.util.Stringable;

public class ExitOptions extends Stringable {
    private final long startTime;
    private final boolean forceSuccessExit;
    private final boolean shouldExit;

    public ExitOptions(long startTime, boolean forceSuccessExit, boolean shouldExit) {
        this.startTime = startTime;
        this.forceSuccessExit = forceSuccessExit;
        this.shouldExit = shouldExit;
    }

    public long getStartTime() {
        return startTime;
    }

    public boolean shouldForceSuccessExit() {
        return forceSuccessExit;
    }

    public boolean shouldExit() {
        return shouldExit;
    }
}
