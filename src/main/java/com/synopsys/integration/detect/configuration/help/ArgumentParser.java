package com.synopsys.integration.detect.configuration.help;

import java.util.Arrays;

public class ArgumentParser {
    private final String[] args;

    public ArgumentParser(String[] args) {
        this.args = args;
    }

    public boolean isArgumentPresent(String command, String largeCommand) {
        return Arrays.stream(args).anyMatch(arg -> arg.equals(command) || arg.equals(largeCommand));
    }

    public String findValueForCommand(String command, String largeCommand) {
        for (int i = 1; i < args.length; i++) {
            String previousArgument = args[i - 1];
            String possibleValue = args[i];
            if ((command.equals(previousArgument) || largeCommand.equals(previousArgument)) && isValueAcceptable(possibleValue)) {
                return possibleValue;
            }
        }
        return null;
    }

    private boolean isValueAcceptable(String value) {
        return !value.startsWith("-");
    }
}
