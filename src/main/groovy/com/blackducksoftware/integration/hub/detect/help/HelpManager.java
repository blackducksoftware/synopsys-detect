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

import java.io.Console;
import java.io.PrintStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.help.print.HelpHtmlWriter;
import com.blackducksoftware.integration.hub.detect.help.print.HelpPrinter;
import com.blackducksoftware.integration.hub.detect.interactive.InteractiveManager;
import com.blackducksoftware.integration.hub.detect.interactive.reader.ConsoleInteractiveReader;
import com.blackducksoftware.integration.hub.detect.interactive.reader.InteractiveReader;
import com.blackducksoftware.integration.hub.detect.interactive.reader.ScannerInteractiveReader;

@Component
public class HelpManager {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    HelpHtmlWriter helpHtmlWriter;

    @Autowired
    InteractiveManager interactiveManager;

    public boolean isHelpMessageApplicable(final String[] applicationArgs) {
        return isCommand(applicationArgs, "-h", "--help");
    }

    public boolean isHelpDocumentApplicable(final String[] applicationArgs) {
        return isCommand(applicationArgs, "-hdoc", "--helpdocument");
    }

    public boolean isInteractiveModeApplicable(final String[] applicationArgs) {
        return isCommand(applicationArgs, "-i", "--interactive");
    }

    private boolean isCommand(final String[] applicationArgs, final String command, final String longCommand) {
        return applicationArgs.length >= 1 && (command.equals(applicationArgs[0]) || longCommand.equals(applicationArgs[0]));
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
                    .filter(option -> DetectConfiguration.GROUP_HUB_CONFIGURATION.equals(option.getGroup()))
                    .collect(Collectors.toList());
            helpPrinter.printHelpMessage(filteredCommonOptions);
        }
    }

    public void writeHelpMessage(final String detectVersion) {
        helpHtmlWriter.writeHelpMessage(String.format("hub-detect-%s-help.html", detectVersion));
    }

    public void runInteractiveMode() {
        final InteractiveReader interactiveReader = createInteractiveReader();
        final PrintStream interactivePrintStream = new PrintStream(System.out);
        interactiveManager.interact(interactiveReader, interactivePrintStream);
    }

    private InteractiveReader createInteractiveReader() {
        final Console console = System.console();
        if (console != null) {
            return new ConsoleInteractiveReader(console);
        } else {
            logger.warn("It may be insecure to enter passwords because you are running in a virtual console.");
            return new ScannerInteractiveReader(System.in);
        }
    }
}
