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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.help.DetectOption;

@Component
public class HelpPrinter {

    public void printHelpMessage(final PrintStream printStream, final List<DetectOption> options, String filterGroup) {
        final HelpTextWriter writer = new HelpTextWriter();
        writer.println();
        
        Set<String> groups = new HashSet<String>();
        boolean filterByGroup = false; // must match at least one group
        if (filterGroup != null && filterGroup.trim().length() >= 0) {
            for (final DetectOption detectValue : options) {
                for (final String printGroup : detectValue.getPrintGroups()) {
                    if (printGroup.equalsIgnoreCase(filterGroup)) {
                        filterByGroup = true;
                    }
                    groups.add(printGroup);
                }
            }
        }
        

        writer.printColumns(Arrays.asList("Property Name", "Default", "Description"));
        writer.printSeperator();
        
        String groupText = "";
        List<String> groupList = new ArrayList<String>(groups);
        java.util.Collections.sort(groupList);
        for (String group : groupList) {
            if (group.contains(" ")) continue;
            if (!groupText.equals("")) {
                groupText += ", ";
            }
            groupText += group;
        }
        
        if (filterByGroup) {
            writer.println("Showing help only for: " + filterGroup);
            writer.println();
        }
        
        String group = null;

        for (final DetectOption detectValue : options) {
            final String currentGroup = detectValue.getGroup();
            
            if (filterByGroup) {
                boolean inAnyGroup = false;
                for (final String printGroup : detectValue.getPrintGroups()) {
                    inAnyGroup = inAnyGroup || printGroup.equalsIgnoreCase(filterGroup);
                }
                if (!inAnyGroup) continue;
            }
            
            if (group == null) {
                group = currentGroup;
            } else if (!group.equals(currentGroup)) {
                writer.println();
                group = currentGroup;
            }

            writer.printColumns(Arrays.asList("--" + detectValue.getKey(), detectValue.getDefaultValue(), detectValue.getDescription()));
        }
        
        writer.println();
        writer.println("Usage : ");
        writer.println("\t--<property name>=<value>");
        writer.println();
        writer.println("To print only a subset of options, you may specify one of the following printable groups with '-h [group]' or '--help [group]': ");
        writer.println("\t" + groupText);
        writer.println();        
        writer.println("To search options, you may specify a search term followed by * '-h [term]*' or '--help [term]*': ");
        writer.println();

        writer.write(printStream);
    }

}
