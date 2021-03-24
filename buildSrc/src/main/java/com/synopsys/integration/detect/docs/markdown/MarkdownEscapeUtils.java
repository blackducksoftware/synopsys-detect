/*
 * buildSrc
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.docs.markdown;

import org.apache.commons.lang3.StringUtils;

// Escapes using a backslash all supported characters in a markdown text literal based on: https://daringfireball.net/projects/markdown/syntax#backslash
class MarkdownEscapeUtils {
    private MarkdownEscapeUtils() { }

    private static final char[] characters = "\\`*_{}[]()#+-.!".toCharArray();

    public static String escape(final String text) {
        String cleanedText = text;
        for (final char character : characters) {
            cleanedText = StringUtils.replace(cleanedText, String.valueOf(character), "\\" + character);
        }
        return cleanedText;
    }
}