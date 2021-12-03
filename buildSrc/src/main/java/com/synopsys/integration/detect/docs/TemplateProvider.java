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

    public TemplateProvider(File templateDirectory, String projectVersion) throws IOException, TemplateModelException {

        configuration.setDirectoryForTemplateLoading(templateDirectory);
        configuration.setDefaultEncoding("UTF-8");
        configuration.setRegisteredCustomOutputFormats(Collections.singletonList(MarkdownOutputFormat.INSTANCE));

        Terms terms = new Terms();
        terms.put("program_version", projectVersion);
        configuration.setSharedVaribles(terms.getTerms());
    }

    public Template getTemplate(String templateName) throws IOException {
        return configuration.getTemplate(templateName);
    }
}
