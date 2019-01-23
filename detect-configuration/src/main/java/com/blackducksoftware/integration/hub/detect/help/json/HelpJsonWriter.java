/**
 * detect-configuration
 *
 * Copyright (C) 2019 Black Duck Software, Inc.
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
package com.blackducksoftware.integration.hub.detect.help.json;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.help.DetectOption;
import com.blackducksoftware.integration.hub.detect.help.DetectOptionHelp;
import com.google.gson.Gson;

import freemarker.template.Configuration;

public class HelpJsonWriter {
    private final Logger logger = LoggerFactory.getLogger(com.blackducksoftware.integration.hub.detect.help.html.HelpHtmlWriter.class);

    private final Configuration configuration;
    private final Gson gson;

    public HelpJsonWriter(final Configuration configuration, Gson gson) {
        this.configuration = configuration;
        this.gson = gson;
    }

    public void writeGsonDocument(final String filename, List<DetectOption> detectOptions) {
        final HelpJsonData data = new HelpJsonData();

        for (DetectOption option : detectOptions) {
            HelpJsonOption helpJsonOption = convertOption(option);
            data.options.add(helpJsonOption);
        }

        try {
            try (Writer writer = new FileWriter(filename)) {
                gson.toJson(data, writer);
            }

            logger.info(filename + " was created in your current directory.");
        } catch (final IOException e) {
            logger.error("There was an error when creating the html file", e);
        }
    }

    public HelpJsonOption convertOption(DetectOption detectOption) {
        HelpJsonOption helpJsonOption = new HelpJsonOption();

        DetectProperty property = detectOption.getDetectProperty();
        helpJsonOption.propertyName = property.getPropertyName();
        helpJsonOption.propertyKey = property.getPropertyKey();
        helpJsonOption.propertyType = property.getPropertyType().getDisplayName();
        helpJsonOption.addedInVersion = property.getAddedInVersion();
        helpJsonOption.defaultValue = property.getDefaultValue();

        DetectOptionHelp optionHelp = detectOption.getDetectOptionHelp();
        helpJsonOption.group = optionHelp.primaryGroup;
        helpJsonOption.additionalGroups = optionHelp.groups;
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
