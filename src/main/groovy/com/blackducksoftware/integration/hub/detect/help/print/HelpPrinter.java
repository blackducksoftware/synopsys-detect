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

import java.io.PrintStream;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.help.DetectOption;

@Component
public class HelpPrinter {

    public void printHelpMessage(final PrintStream printStream, final List<DetectOption> options, String filterGroup) {
        final HelpTextWriter writer = new HelpTextWriter();
        writer.println();
        
        List<String> printGroups = options.stream()
                .flatMap(it -> it.getPrintGroupsAsList().stream())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        
        String groupText = printGroups.stream().collect(Collectors.joining(","));

        String notes = null;
        List<DetectOption> filteredOptions;
        
        boolean filterByGroup = printGroups.contains(filterGroup);
        if (filterByGroup) {
            
            notes = "Showing help only for: " + filterGroup;
            
            filteredOptions = options.stream()
                    .filter(it -> it.getPrintGroupsAsList().stream().anyMatch(printGroup -> printGroup.equalsIgnoreCase(filterGroup)))
                    .collect(Collectors.toList());
            
        }else if (filterGroup.endsWith("*")) {
            
            String searchTerm = filterGroup.substring(0, filterGroup.length() - 1).toLowerCase();
            notes = "Showing help only for fields that contain: " + searchTerm;

            filteredOptions = options.stream()
                    .filter(it -> it.getKey().contains(searchTerm))
                    .collect(Collectors.toList());
            
        }else {
            filteredOptions = options;
        }
        
        
        printOptions(writer, filteredOptions, groupText, notes);
        
        writer.write(printStream);

    }
    

    private void printOptions(HelpTextWriter writer, List<DetectOption> options, String groupText, String notes) {
        writer.printColumns("Property Name", "Default", "Description");
        writer.printSeperator();

        if (notes != null) {
            writer.println(notes);
            writer.println();
        }
        
        String group = null;
        for (final DetectOption detectValue : options) {
            final String currentGroup = detectValue.getGroup();
            if (group == null) {
                group = currentGroup;
            } else if (!group.equals(currentGroup)) {
                writer.println();
                group = currentGroup;
            }
            writer.printColumns("--" + detectValue.getKey(), detectValue.getDefaultValue(), detectValue.getDescription());
        }
        
        writer.println();
        writer.println("Usage : ");
        writer.println("\t--<property name>=<value>");
        writer.println();
        writer.println("To print only a subset of options, you may specify one of the following printable groups with '-h [group]' or '--help [group]': ");
        writer.println("\t" + groupText);
        writer.println();        
        writer.println("To search options, you may specify a search term followed by * with '-h [term]*' or '--help [term]*': ");
        writer.println();

    }

}
