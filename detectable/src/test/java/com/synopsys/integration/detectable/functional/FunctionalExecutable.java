package com.synopsys.integration.detectable.functional;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.synopsys.integration.executable.Executable;

//TODO: Used because Executable can't be put in a map. Change when INTCMN-471 resolved.
public class FunctionalExecutable {
    private final Executable referencedExecutable; //NOT used in equal and hash code.
    private final File workingDirectory;
    private final Map<String, String> environmentVariables;
    private final List<String> commandWithArguments;

    public FunctionalExecutable(Executable executable) {
        referencedExecutable = executable;
        workingDirectory = executable.getWorkingDirectory();
        environmentVariables = executable.getEnvironmentVariables();
        commandWithArguments = executable.getCommandWithArguments();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        FunctionalExecutable that = (FunctionalExecutable) o;
        return Objects.equals(workingDirectory, that.workingDirectory) &&
            Objects.equals(environmentVariables, that.environmentVariables) &&
            Objects.equals(commandWithArguments, that.commandWithArguments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(workingDirectory, environmentVariables, commandWithArguments);
    }

    public Executable getReferencedExecutable() {
        return referencedExecutable;
    }
}
