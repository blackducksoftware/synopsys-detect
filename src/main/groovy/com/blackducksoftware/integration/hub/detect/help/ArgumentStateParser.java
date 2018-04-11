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

import org.springframework.stereotype.Component;

@Component
public class ArgumentStateParser {

    
    public ArgumentState parseArgs(String[] args) {
        boolean isHelp = checkFirstArgument("-h", "--help", args);
        boolean isHelpDocument = checkFirstArgument("-hdoc", "--helpdocument", args);
        boolean isInteractive = checkFirstArgument("-i", "--interactive", args);
        
        boolean isVerboseHelpMessage = isHelp && checkSecondArgument("-v", "--verbose", args);
        
        String parsedValue = null;
        if (isHelp && !(isVerboseHelpMessage)){
            if (args.length == 2) {
                parsedValue = args[1];
            }
        }
        
        return new ArgumentState(isHelp, isHelpDocument, isInteractive, isVerboseHelpMessage, parsedValue);
    }
    
    private boolean checkFirstArgument(final String command, final String largeCommand, String[] args) {
        return checkArgument(command, largeCommand, 0, args);
    }

    private boolean checkSecondArgument(final String command, final String largeCommand, String[] args) {
        return checkArgument(command, largeCommand, 1, args);
    }
     
    
    private boolean checkArgument(final String command, final String largeCommand, int index, String[] args) {
        if (args.length > index) {
            return (command.equals(args[index]) || largeCommand.equals(args[index]));
        }

        return false;
    }
    
}
