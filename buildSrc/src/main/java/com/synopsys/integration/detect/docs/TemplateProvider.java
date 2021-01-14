/**
 * buildSrc
 *
 * Copyright (c) 2021 Synopsys, Inc.
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
package com.synopsys.integration.detect.docs;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import com.synopsys.integration.detect.docs.content.Terms;
import com.synopsys.integration.detect.docs.markdown.MarkdownOutputFormat;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateModelException;

public class TemplateProvider {
    private final Configuration configuration = new Configuration(Configuration.VERSION_2_3_26);

    public TemplateProvider(final File templateDirectory, final String projectVersion) throws IOException, TemplateModelException {

        configuration.setDirectoryForTemplateLoading(templateDirectory);
        configuration.setDefaultEncoding("UTF-8");
        configuration.setRegisteredCustomOutputFormats(Collections.singletonList(MarkdownOutputFormat.INSTANCE));

        final Terms terms = new Terms();
        terms.put("program_version", projectVersion);
        configuration.setSharedVaribles(terms.getTerms());
    }

    public Template getTemplate(final String templateName) throws IOException {
        return configuration.getTemplate(templateName);
    }
}
