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
package com.blackducksoftware.integration.hub.detect.help.html;

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

import com.blackducksoftware.integration.hub.detect.help.DetectBaseOption;
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

    @Autowired
    Configuration configuration;

    public void writeHelpMessage(final String filename) {
        final HelpHtmlDataBuilder builder = new HelpHtmlDataBuilder();
        
        for (DetectBaseOption option : detectOptionManager.getDetectOptions()) {
            builder.addDetectOption(option);
        }
        final HelpHtmlData templateData = builder.build();

        try {
            final File htmlHelpFile = new File(filename);
            final Template htmlTemplate = configuration.getTemplate("templates/helpHtml.ftl");
            htmlTemplate.process(templateData, new FileWriter(htmlHelpFile));
            logger.info(filename + " was created in your current directory.");
        } catch (final IOException | TemplateException e) {
            logger.error("There was an error when creating the html file", e);
        }
    }
}
