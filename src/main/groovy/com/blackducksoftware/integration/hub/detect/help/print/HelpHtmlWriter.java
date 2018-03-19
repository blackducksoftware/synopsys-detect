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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.help.DetectOption;
import com.blackducksoftware.integration.hub.detect.help.DetectOptionManager;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

@Component
public class HelpHtmlWriter {
    private final Logger logger = LoggerFactory.getLogger(HelpHtmlWriter.class);

    @Autowired
    DetectOptionManager detectOptionManager;

    public void writeHelpMessage(final String fileName) {
        final Configuration configuration = new Configuration(Configuration.VERSION_2_3_26);
        configuration.setClassForTemplateLoading(getClass(), "/");
        configuration.setDefaultEncoding("UTF-8");

        final List<GroupOptionListing> groupOptions = new ArrayList<>();

        for (final String groupName : detectOptionManager.getDetectGroups()) {
            final List<DetectOption> filteredOptions = getGroupDetectOptions(groupName);
            groupOptions.add(new GroupOptionListing(StringUtils.capitalise(groupName), filteredOptions));
        }

        final Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("options", groupOptions);
        try {
            final File htmlHelpFile = new File(fileName);
            final Template htmlTemplate = configuration.getTemplate("HelpHtml.ftl");
            htmlTemplate.process(dataModel, new FileWriter(htmlHelpFile));
            logger.info(fileName + " was created in your working directory.");
        } catch (final IOException | TemplateException e) {
            logger.error("There was an error when creating the html file", e);
        }
    }

    private List<DetectOption> getGroupDetectOptions(final String group) {
        final List<DetectOption> detectOptions = detectOptionManager.getDetectOptions();
        final List<DetectOption> filteredOptions = detectOptions.stream()
                .filter(option -> group.equals(option.getGroup()))
                .collect(Collectors.toList());
        return filteredOptions;
    }
}
