package com.synopsys.integration.detect.lifecycle.shutdown;

public interface ExitCodePublisher {
    void publishExitCode(ExitCodeRequest exitCodeRequest);
}
