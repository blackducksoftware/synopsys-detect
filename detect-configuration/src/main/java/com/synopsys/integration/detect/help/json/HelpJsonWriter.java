/**
 * detect-configuration
 *
 * Copyright (c) 2019 Synopsys, Inc.
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
package com.synopsys.integration.detect.help.json;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.EnumUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.synopsys.integration.detect.configuration.DetectProperty;
import com.synopsys.integration.detect.exitcode.ExitCodeType;
import com.synopsys.integration.detect.help.DetectOption;
import com.synopsys.integration.detect.help.DetectOptionHelp;

import freemarker.template.Configuration;

public class HelpJsonWriter {
    private final Logger logger = LoggerFactory.getLogger(HelpJsonWriter.class);

    private final Configuration configuration;
    private final Gson gson;

    public HelpJsonWriter(final Configuration configuration, final Gson gson) {
        this.configuration = configuration;
        this.gson = gson;
    }

    public void writeGsonDocument(final String filename, final List<DetectOption> detectOptions, List<HelpJsonDetector> buildDetectors, List<HelpJsonDetector> buildlessDetectors) {
        final HelpJsonData data = new HelpJsonData();

        data.options.addAll(detectOptions.stream().map(this::convertOption).collect(Collectors.toList()));
        data.exitCodes.addAll(Stream.of(ExitCodeType.values()).map(this::convertExitCode).collect(Collectors.toList()));
        data.buildlessDetectors = buildlessDetectors;
        data.buildDetectors = buildDetectors;

        try {
            try (final Writer writer = new FileWriter(filename)) {
                gson.toJson(data, writer);
            }

            logger.info(filename + " was created in your current directory.");
        } catch (final IOException e) {
            logger.error("There was an error when creating the html file", e);
        }
    }

    public HelpJsonExitCode convertExitCode(final ExitCodeType exitCodeType) {
        HelpJsonExitCode helpJsonExitCode = new HelpJsonExitCode();
        helpJsonExitCode.exitCodeKey = exitCodeType.name();
        helpJsonExitCode.exitCodeValue = exitCodeType.getExitCode();
        helpJsonExitCode.exitCodeDescription = exitCodeType.getDescription();
        return helpJsonExitCode;
    }

    public HelpJsonOption convertOption(final DetectOption detectOption) {
        final HelpJsonOption helpJsonOption = new HelpJsonOption();

        final DetectProperty property = detectOption.getDetectProperty();
        helpJsonOption.propertyName = property.getPropertyName();
        helpJsonOption.propertyKey = property.getPropertyKey();
        helpJsonOption.propertyType = property.getPropertyType().getDisplayName();
        helpJsonOption.addedInVersion = property.getAddedInVersion();
        helpJsonOption.defaultValue = property.getDefaultValue();

        final DetectOptionHelp optionHelp = detectOption.getDetectOptionHelp();
        helpJsonOption.group = optionHelp.primaryGroup;
        helpJsonOption.additionalGroups = optionHelp.additionalGroups;
        helpJsonOption.category = optionHelp.category;
        helpJsonOption.description = optionHelp.description;
        helpJsonOption.detailedDescription = optionHelp.detailedHelp;
        helpJsonOption.deprecated = optionHelp.isDeprecated;
        if (optionHelp.isDeprecated) {
            helpJsonOption.deprecatedDescription = optionHelp.deprecation;
            helpJsonOption.deprecatedFailInVersion = optionHelp.deprecationFailInVersion.getDisplayValue();
            helpJsonOption.deprecatedRemoveInVersion = optionHelp.deprecationRemoveInVersion.getDisplayValue();
        }
        helpJsonOption.strictValues = detectOption.hasStrictValidation();
        helpJsonOption.caseSensitiveValues = detectOption.hasCaseSensitiveValidation();
        helpJsonOption.acceptableValues = detectOption.getValidValues();
        helpJsonOption.hasAcceptableValues = detectOption.getValidValues().size() > 0;
        helpJsonOption.isCommaSeparatedList = detectOption.isCommaSeperatedList();
        return helpJsonOption;
    }
}
