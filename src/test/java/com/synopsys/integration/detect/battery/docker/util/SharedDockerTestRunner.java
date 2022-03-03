package com.synopsys.integration.detect.battery.docker.util;

import com.synopsys.integration.detect.battery.docker.integration.BlackDuckAssertions;
import com.synopsys.integration.detect.battery.docker.integration.BlackDuckTestConnection;

public class SharedDockerTestRunner {
    public DetectDockerTestRunner runner;
    public BlackDuckTestConnection blackDuckTestConnection;
    public BlackDuckAssertions blackDuckAssertions;
    public DetectCommandBuilder command;

    public SharedDockerTestRunner(
        DetectDockerTestRunner runner,
        BlackDuckTestConnection blackDuckTestConnection,
        BlackDuckAssertions blackDuckAssertions,
        DetectCommandBuilder detectCommandBuilder
    ) {
        this.runner = runner;
        this.blackDuckTestConnection = blackDuckTestConnection;
        this.blackDuckAssertions = blackDuckAssertions;
        this.command = detectCommandBuilder;
    }

    public DockerAssertions run() {
        return runner.run(command);
    }
}
