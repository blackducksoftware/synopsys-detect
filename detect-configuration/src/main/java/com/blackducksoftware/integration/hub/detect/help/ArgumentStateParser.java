/**
 * detect-configuration
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.blackducksoftware.integration.hub.detect.help;

import java.util.Arrays;

import org.springframework.stereotype.Component;

@Component
public class ArgumentStateParser {

    public ArgumentState parseArgs(String[] args) {
        boolean isHelp = checkArgumentPresent("-h", "--help", args);
        boolean isHelpDocument = checkArgumentPresent("-hdoc", "--helpdocument", args);
        boolean isInteractive = checkArgumentPresent("-i", "--interactive", args);

        boolean isVerboseHelp = checkArgumentPresent("-hv", "--helpVerbose", args);
        boolean isDeprecatedHelp = checkArgumentPresent("-hd", "--helpDeprecated", args);

        String parsedValue = null;
        if (isHelp) {
            parsedValue = getParsedValueForCommand("-h", "--help", args);
        }

        return new ArgumentState(isHelp, isHelpDocument, isInteractive, isVerboseHelp, isDeprecatedHelp, parsedValue);
    }

    private boolean checkArgumentPresent(final String command, final String largeCommand, final String[] args) {
        return Arrays.stream(args).anyMatch(arg -> arg.equals(command) || arg.equals(largeCommand));
    }

    private String getParsedValueForCommand(final String command, final String largeCommand, final String[] args) {
        for (int i = 1; i < args.length; i++) {
            final String previousArgument = args[i - 1];
            final String possibleValue = args[i];
            if (command.equals(previousArgument) || largeCommand.equals(previousArgument)) {
                if (valueIsAcceptable(possibleValue)) {
                    return possibleValue;
                }
            }
        }
        return null;
    }

    private boolean valueIsAcceptable(final String value) {
        if (!value.startsWith("-")) {
            return true;
        }
        return false;
    }

}
