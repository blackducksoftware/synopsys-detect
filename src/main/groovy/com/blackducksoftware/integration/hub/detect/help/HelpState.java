/**
 * hub-detect
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

public class HelpState {
    String[] args;

    public HelpState(final String[] args) {
        this.args = args;
    }

    public boolean isHelpMessage() {
        return isHelpCommand();
    }

    public boolean isVerboseHelpMessage() {
        return checkSecondCommand("-v", "--verbose");
    }

    public boolean isGroupListingHelpMessage() {
        return checkSecondCommand("-g", "--group");
    }

    public boolean isGroupPropertiesHelpMessage() {
        return args.length == 3 && isGroupListingHelpMessage();
    }

    public boolean isPropertyDetailedHelpMessage() {
        return args.length == 3 && checkSecondCommand("-p", "--property");
    }

    private boolean isHelpCommand() {
        if (args.length >= 1) {
            return "-h".equals(args[0]) || "--help".equals(args[0]);
        }

        return false;
    }

    private boolean checkSecondCommand(final String command, final String largeCommand) {
        if (args.length >= 2) {
            return isHelpCommand() && (command.equals(args[1]) || largeCommand.equals(args[1]));
        }

        return false;
    }
}
