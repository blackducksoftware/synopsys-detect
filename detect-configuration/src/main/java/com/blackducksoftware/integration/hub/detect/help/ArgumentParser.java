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
            if (command.equals(previousArgument) || largeCommand.equals(previousArgument)) {
                if (isValueAcceptable(possibleValue)) {
                    return possibleValue;
                }
            }
        }
        return null;
    }

    private boolean isValueAcceptable(final String value) {
        if (!value.startsWith("-")) {
            return true;
        }
        return false;
    }

}
