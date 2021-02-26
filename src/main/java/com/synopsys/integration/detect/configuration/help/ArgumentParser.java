/**
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.configuration.help;

import java.util.Arrays;

public class ArgumentParser {
    private final String[] args;

    public ArgumentParser(final String[] args) {
        this.args = args;
    }

    public boolean isArgumentPresent(final String command, final String largeCommand) {
        return Arrays.stream(args).anyMatch(arg -> arg.equals(command) || arg.equals(largeCommand));
    }

    public String findValueForCommand(final String command, final String largeCommand) {
        for (int i = 1; i < args.length; i++) {
            final String previousArgument = args[i - 1];
            final String possibleValue = args[i];
            if ((command.equals(previousArgument) || largeCommand.equals(previousArgument)) && isValueAcceptable(possibleValue)) {
                return possibleValue;
            }
        }
        return null;
    }

    private boolean isValueAcceptable(final String value) {
        return !value.startsWith("-");
    }
}
