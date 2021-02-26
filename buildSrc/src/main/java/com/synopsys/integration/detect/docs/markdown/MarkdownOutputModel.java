/*
 * buildSrc
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.docs.markdown;

import org.jetbrains.annotations.NotNull;

import freemarker.core.CommonMarkupOutputFormat;
import freemarker.core.CommonTemplateMarkupOutputModel;

class MarkdownOutputModel extends CommonTemplateMarkupOutputModel<MarkdownOutputModel> {
    /**
     * A least one of the parameters must be non-{@code null}!
     */
    protected MarkdownOutputModel(@NotNull final String plainTextContent, @NotNull final String markupContent) {
        super(plainTextContent, markupContent);
    }

    @Override
    public CommonMarkupOutputFormat<MarkdownOutputModel> getOutputFormat() {
        return MarkdownOutputFormat.INSTANCE;
    }
}