package com.synopsys.integration.detect.tool.detector;

import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detectable.detectable.executable.Executable;
import com.synopsys.integration.detectable.detectable.executable.ExecutableOutput;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunnerException;
import com.synopsys.integration.detectable.detectable.executable.impl.SimpleExecutableRunner;

public class DetectExecutableRunner extends SimpleExecutableRunner {
    private final EventSystem eventSystem;

    public DetectExecutableRunner(final EventSystem eventSystem) {
        this.eventSystem = eventSystem;
    }

    @Override
    public ExecutableOutput execute(final Executable executable) throws ExecutableRunnerException {
        final ExecutableOutput output = super.execute(executable);
        eventSystem.publishEvent(Event.Executable, output);
        return output;
    }
}
