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
package com.synopsys.integration.detect.help.html;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.help.DetectOption;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class HelpHtmlWriter {
    private final Logger logger = LoggerFactory.getLogger(HelpHtmlWriter.class);

    private final Configuration configuration;

    public HelpHtmlWriter(final Configuration configuration) {
        this.configuration = configuration;
    }

    public void writeHtmlDocument(final String filename, List<DetectOption> detectOptions) {
        final HelpHtmlDataBuilder builder = new HelpHtmlDataBuilder();

        for (DetectOption option : detectOptions) {
            builder.addDetectOption(option);
        }
        final HelpHtmlData templateData = builder.build();

        try {
            final File htmlHelpFile = new File(filename);
            final Template htmlTemplate = configuration.getTemplate("templates/helpHtml.ftl");
            htmlTemplate.process(templateData, new FileWriter(htmlHelpFile));
            logger.info(filename + " was created in your current directory.");
            logger.info(htmlHelpFile.getAbsolutePath() + " was created.");
        } catch (final IOException | TemplateException e) {
            logger.error("There was an error when creating the html file", e);
        }
    }
}
