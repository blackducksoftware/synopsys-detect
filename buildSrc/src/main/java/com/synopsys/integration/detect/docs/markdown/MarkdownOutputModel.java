package com.synopsys.integration.detect.docs.markdown;

import org.jetbrains.annotations.NotNull;

import freemarker.core.CommonMarkupOutputFormat;
import freemarker.core.CommonTemplateMarkupOutputModel;

class MarkdownOutputModel extends CommonTemplateMarkupOutputModel<MarkdownOutputModel> {
    /**
     * A least one of the parameters must be non-{@code null}!
     */
    protected MarkdownOutputModel(@NotNull String plainTextContent, @NotNull String markupContent) {
        super(plainTextContent, markupContent);
    }

    @Override
    public CommonMarkupOutputFormat<MarkdownOutputModel> getOutputFormat() {
        return MarkdownOutputFormat.INSTANCE;
    }
}