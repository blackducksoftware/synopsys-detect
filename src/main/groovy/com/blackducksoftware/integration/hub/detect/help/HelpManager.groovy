/*
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
package com.blackducksoftware.integration.hub.detect.help

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.detect.DetectConfiguration
import com.blackducksoftware.integration.hub.detect.help.print.HelpPrinter

import groovy.transform.TypeChecked

@Component
@TypeChecked
class HelpManager {
    private final Logger logger = LoggerFactory.getLogger(HelpManager.class)
    public boolean isHelpMessageApplicable(String[] applicationArgs) {
        '-h' in applicationArgs || '--help' in applicationArgs
    }

    public void printAppropriateHelpMessage(String[] applicationArgs, List<DetectOption> detectOptions) {
        HelpPrinter helpPrinter = new HelpPrinter(System.out);
        if (applicationArgs.size() == 1) {
            List<DetectOption> options = detectOptions.findAll {
                DetectConfiguration.GROUP_COMMON.equals(it.group)
            }
            printSimpleHelp(helpPrinter, options)
        } else if (applicationArgs.size() == 2) {
            String secondArg = applicationArgs[1]
            if ('-v'.equals(secondArg) || '--verbose'.equals(secondArg)) {
                printVerboseHelp(helpPrinter, detectOptions)
            } else {
                DetectOption detectOption = detectOptions?.find {
                    it.key.equals(secondArg)
                }
                printDetailedHelp(helpPrinter, detectOption)
            }
        }
    }

    public void printSimpleHelp(HelpPrinter helpPrinter, List<DetectOption> detectOptions) {
        helpPrinter.printVerboseMessage()
        helpPrinter.printHelpMessage(detectOptions)
    }

    public void printVerboseHelp(HelpPrinter helpPrinter, List<DetectOption> detectOptions) {
        helpPrinter.printHelpMessage(detectOptions)
    }

    public void printDetailedHelp(HelpPrinter helpPrinter, DetectOption detectOption) {
        helpPrinter.printHelpDetailedMessage(detectOption)
    }
}
