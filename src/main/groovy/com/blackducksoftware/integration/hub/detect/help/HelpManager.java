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

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.help.print.HelpPrinter;

@Component
public class HelpManager {
    Map<String, List<String>> commandToAction;

    public boolean isHelpMessageApplicable(final String[] applicationArgs) {
        final HelpState helpState = new HelpState(applicationArgs);
        return helpState.isHelpMessage();
    }

    public void printAppropriateHelpMessage(final String[] applicationArgs, final DetectOptionManager detectOptionManager) {
        final HelpPrinter helpPrinter = new HelpPrinter(System.out);
        final HelpState helpState = new HelpState(applicationArgs);

        if (helpState.isPropertyDetailedHelpMessage()) {
            final List<DetectOption> options = detectOptionManager.getDetectOptions();
            final Optional<DetectOption> filteredPropertyOption = options.stream()
                    .filter(option -> option.getKey().equals(applicationArgs[2]))
                    .findFirst();
            if (filteredPropertyOption.isPresent()) {
                helpPrinter.printHelpDetailedMessage(filteredPropertyOption.get());
            } else {
                helpPrinter.printHelpMessage(options);
            }
        } else if (helpState.isGroupPropertiesHelpMessage()) {
            final List<DetectOption> options = detectOptionManager.getDetectOptions();
            final List<DetectOption> filteredGroupOptions = options.stream()
                    .filter(option -> option.getGroup().equals(applicationArgs[2]))
                    .collect(Collectors.toList());
            helpPrinter.printHelpMessage(filteredGroupOptions);
        } else if (helpState.isGroupListingHelpMessage()) {
            final List<String> groups = detectOptionManager.getDetectGroups();
            helpPrinter.printHelpGroupsMessage(groups.stream().sorted().collect(Collectors.toList()));
        } else if (helpState.isVerboseHelpMessage()) {
            final List<DetectOption> options = detectOptionManager.getDetectOptions();
            helpPrinter.printHelpMessage(options);
        } else {
            helpPrinter.printVerboseMessage();
            final List<DetectOption> options = detectOptionManager.getDetectOptions();
            final List<DetectOption> filteredCommonOptions = options.stream()
                    .filter(option -> DetectConfiguration.GROUP_COMMON.equals(option.getGroup()))
                    .collect(Collectors.toList());
            helpPrinter.printHelpMessage(filteredCommonOptions);
        }
    }
}
