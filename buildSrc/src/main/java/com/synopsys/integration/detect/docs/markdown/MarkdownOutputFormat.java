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
    public void output(@NotNull String textToEsc, @NotNull Writer out) throws IOException {
        String escapedText = MarkdownEscapeUtils.escape(textToEsc);
        out.write(escapedText);
    }

    @Override
    public String escapePlainText(@Nullable String plainTextContent) {
        return MarkdownEscapeUtils.escape(Optional.ofNullable(plainTextContent).orElse(""));
    }

    @Override
    public boolean isLegacyBuiltInBypassed(@Nullable String builtInName) {
        return "markdown".equals(builtInName);
    }

    @Override
    protected MarkdownOutputModel newTemplateMarkupOutputModel(String plainTextContent, String markupContent) {
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