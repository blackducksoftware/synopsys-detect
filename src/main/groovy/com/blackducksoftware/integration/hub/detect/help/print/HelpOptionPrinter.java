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

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.help.DetectOption;

@Component
public class HelpOptionPrinter {

    public void printOptions(HelpTextWriter writer, List<DetectOption> options, String notes) {
        writer.printColumns("Property Name", "Default", "Description");
        writer.printSeperator();

        if (notes != null) {
            writer.println(notes);
            writer.println();
        }
        
        String group = null;
        for (final DetectOption detectOption : options) {
            final String currentGroup = detectOption.getHelp().primaryGroup;
            if (group == null) {
                group = currentGroup;
            } else if (!group.equals(currentGroup)) {
                writer.println();
                group = currentGroup;
            }
            String description = detectOption.getHelp().description;
            if (detectOption.getAcceptableValues().size() > 0) {
                description += " (" + detectOption.getAcceptableValues().stream().collect(Collectors.joining("|")) + ")";
            }
            writer.printColumns("--" + detectOption.getKey(), detectOption.getDefaultValue(), description);
        }
    }
    
    public void printStandardFooter(HelpTextWriter writer, String groupText) {
        writer.println();
        writer.println("Usage : ");
        writer.println("\t--<property name>=<value>");
        writer.println();
        writer.println("To see all properties, you may request verbose help log with '-h -v'");
        writer.println();
        writer.println("To get detailed help for a specific property, you may specify the property name with '-h [property]' or '-h -p [property]'");
        writer.println();
        writer.println("To print only a subset of options, you may specify one of the following printable groups with '-h [group]' or '-h -g [group]': ");
        writer.println("\t" + groupText);
        writer.println();        
        writer.println("To search options, you may specify a search term with '-h [term]'");
        writer.println();
    }
}
