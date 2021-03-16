/*
 * buildSrc
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
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
