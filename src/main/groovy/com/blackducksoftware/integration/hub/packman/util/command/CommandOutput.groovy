package com.blackducksoftware.integration.hub.packman.util.command

class CommandOutput {
    final String standardOutput
    final String errorOutput

    public CommandOutput(final String standardOutput, final String errorOutput) {
        this.standardOutput = standardOutput
        this.errorOutput = errorOutput
    }
}
