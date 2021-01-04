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
package com.synopsys.integration.detect.docs.markdown;

import java.io.IOException;
import java.io.Writer;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import freemarker.core.CommonMarkupOutputFormat;

// This class was made by following the guideline of RTFOutputFormat as proper docs on how to do this could not be found. This may be incorrect, but seems to work.
public class MarkdownOutputFormat extends CommonMarkupOutputFormat<MarkdownOutputModel> {
    public static final MarkdownOutputFormat INSTANCE = new MarkdownOutputFormat();

    @Override
    public void output(@NotNull final String textToEsc, @NotNull final Writer out) throws IOException {
        final String escapedText = MarkdownEscapeUtils.escape(textToEsc);
        out.write(escapedText);
    }

    @Override
    public String escapePlainText(@Nullable final String plainTextContent) {
        return MarkdownEscapeUtils.escape(Optional.ofNullable(plainTextContent).orElse(""));
    }

    @Override
    public boolean isLegacyBuiltInBypassed(@Nullable final String builtInName) {
        return "markdown".equals(builtInName);
    }

    @Override
    protected MarkdownOutputModel newTemplateMarkupOutputModel(final String plainTextContent, final String markupContent) {
        return new MarkdownOutputModel(plainTextContent, markupContent);
    }

    @Override
    public String getName() {
        return "Markdown";
    }

    @Override
    public String getMimeType() {
        return "text/markdown";
    }
}