package com.blackducksoftware.integration.hub.packman.util.executable

class ExecutableOutput {
    final String standardOutput
    final String errorOutput

    public ExecutableOutput(final String standardOutput, final String errorOutput) {
        this.standardOutput = standardOutput
        this.errorOutput = errorOutput
    }
}
