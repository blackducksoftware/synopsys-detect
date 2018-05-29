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

import java.util.stream.Collectors;

import org.codehaus.plexus.util.StringUtils;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.help.DetectBaseOption;
import com.blackducksoftware.integration.hub.detect.help.DetectOption;
import com.blackducksoftware.integration.hub.detect.help.DetectOptionHelp;

@Component
public class HelpDetailedOptionPrinter {

    public void printDetailedOption(final HelpTextWriter writer, final DetectBaseOption detectOption) {
        writer.println("");
        writer.println("Detailed information for " + detectOption.getKey());
        writer.println("");
        if (detectOption.getDetectOptionHelp().isDeprecated) {
            writer.println("Deprecated: will be removed in version " + detectOption.getDetectOptionHelp().deprecationVersion);
            writer.println("");
        }
        writer.println("Property description: " + detectOption.getDetectOptionHelp().description);
        writer.println("Property default value: " + detectOption.getDefaultValue());
        if (detectOption.getAcceptableValues().size() > 0) {
            writer.println("Property acceptable values: " + detectOption.getAcceptableValues().stream().collect(Collectors.joining(", ")));
        }
        writer.println("");

        final DetectOptionHelp help = detectOption.getDetectOptionHelp();
        if (StringUtils.isNotBlank(help.detailedHelp)) {
            writer.println("Detailed help:");
            writer.println(help.detailedHelp);
            writer.println();
        }
    }

}
