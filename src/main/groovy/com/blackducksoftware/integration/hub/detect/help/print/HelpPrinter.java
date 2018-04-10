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
package com.blackducksoftware.integration.hub.detect.help.print;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.help.ArgumentState;
import com.blackducksoftware.integration.hub.detect.help.DetectOption;

@Component
public class HelpPrinter {

    @Autowired
    private HelpOptionPrinter optionPrinter;
    
    @Autowired
    private HelpDetailedOptionPrinter detailPrinter;
    
    public void printAppropriateHelpMessage(final PrintStream printStream, final List<DetectOption> options, ArgumentState state) {
        final HelpTextWriter writer = new HelpTextWriter();
        
        List<String> allPrintGroups = getPrintGroups(options);

        if (state.isVerboseHelpMessage) {
            optionPrinter.printOptions(writer, options, null);
        }else if (state.isGroup) {
            printHelpFilteredByPrintGroup( writer, options, state.parsedValue);
        }else if (state.isGroupList) {
            optionPrinter.printStandardFooter(writer, getPrintGroupText(allPrintGroups));
        }else if (state.isProperty) {
            printDetailedHelp(writer, options, state.parsedValue);
        }else {
            if (state.parsedValue != null) {
                if (isProperty(options, state.parsedValue)) {
                    printDetailedHelp(writer, options, state.parsedValue);
                } else if (isPrintGroup(allPrintGroups, state.parsedValue)){
                    printHelpFilteredByPrintGroup(writer, options, state.parsedValue);
                } else {
                    printHelpFilteredBySearchTerm(writer, options, state.parsedValue);
                }
            }else {
                printDefaultHelp(writer, options);
            }
        }
        
        optionPrinter.printStandardFooter(writer, getPrintGroupText(allPrintGroups));
        
        writer.write(printStream);
    }
    
    private void printDetailedHelp(final HelpTextWriter writer, final List<DetectOption> options, String optionName) {
        DetectOption option = options.stream()
                .filter(it -> it.getKey().equals(optionName))
                .findFirst().orElse(null);
        
        if (option == null) {
            writer.println("Could not find option named: " + optionName);
        } else {
            detailPrinter.printDetailedOption(writer, option);
        }
    }
    
    private void printDefaultHelp(final HelpTextWriter writer, final List<DetectOption> options) {
        printHelpFilteredByPrintGroup(writer, options, DetectConfiguration.PRINT_GROUP_DEFAULT);
    }
    
    private void printHelpFilteredByPrintGroup(final HelpTextWriter writer, final List<DetectOption> options, String filterGroup) {
        String notes = "Showing help only for: " + filterGroup;
        
        List<DetectOption> filteredOptions = options.stream()
                .filter(it -> it.getHelp().groups.stream().anyMatch(printGroup -> printGroup.equalsIgnoreCase(filterGroup)))
                .sorted((o1, o2) -> o1.getKey().compareTo(o2.getKey()))
                .collect(Collectors.toList());
        
        optionPrinter.printOptions(writer, filteredOptions, notes);
    }
    
    private void printHelpFilteredBySearchTerm(final HelpTextWriter writer, final List<DetectOption> options, String searchTerm) {
        String notes = "Showing help only for fields that contain: " + searchTerm;

        List<DetectOption> filteredOptions = options.stream()
                .filter(it -> it.getKey().contains(searchTerm))
                .collect(Collectors.toList());
        
        optionPrinter.printOptions(writer, filteredOptions, notes);
    }
    
    private boolean isPrintGroup (List<String> allPrintGroups, String filterGroup) {
        return allPrintGroups.contains(filterGroup);
    }
    
    private boolean isProperty (List<DetectOption> allOptions, String filterTerm) {
        return allOptions.stream()
                .map(it -> it.getKey())
                .anyMatch(it -> it.equals(filterTerm));
    }
    
    private List<String> getPrintGroups(List<DetectOption> options) {
        return options.stream()
                .flatMap(it -> it.getHelp().groups.stream())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
    
    private String getPrintGroupText(List<String> printGroups) {
        return printGroups.stream().collect(Collectors.joining(","));
    }

}
