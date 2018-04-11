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

public class ArgumentState {
    String[] args;

    public boolean isHelp = false;
    public boolean isHelpDocument = false;
    public boolean isInteractive = false;
    
    public boolean isVerboseHelpMessage = false;
    public boolean isGroup = false;
    public boolean isProperty = false;
    public boolean isGroupList = false;

    public String parsedValue; 
    
    public ArgumentState(final String[] args) {
        this.args = args;
        isHelp = checkFirstArgument("-h", "--help");
        isHelpDocument = checkFirstArgument("-hdoc", "--helpdocument");
        isInteractive = checkFirstArgument("-i", "--interactive");
        
        isVerboseHelpMessage = isHelp && checkSecondArgument("-v", "--verbose");
        isGroup = isHelp && checkSecondArgument("-g", "--group") && parseThirdArgumentValue();
        isProperty = isHelp && checkSecondArgument("-p", "--property") && parseThirdArgumentValue();
        
        if (isHelp && !(isVerboseHelpMessage || isGroup || isProperty)){
            if (args.length == 2) {
                parsedValue = args[1];
            }
        }
    }

    private boolean parseThirdArgumentValue() {
        if (args.length == 3) {
            parsedValue = args[2];
            return true;
        }
        return false;
    }
    
    private boolean checkFirstArgument(final String command, final String largeCommand) {
        return checkArgument(command, largeCommand, 0);
    }

    private boolean checkSecondArgument(final String command, final String largeCommand) {
        return checkArgument(command, largeCommand, 1);
    }
     
    
    private boolean checkArgument(final String command, final String largeCommand, int index) {
        if (args.length > index) {
            return (command.equals(args[index]) || largeCommand.equals(args[index]));
        }

        return false;
    }

}
