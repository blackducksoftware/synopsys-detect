package com.synopsys.integration.detect.docs.markdown;

import org.apache.commons.lang3.StringUtils;

// Escapes using a backslash all supported characters in a markdown text literal based on: https://daringfireball.net/projects/markdown/syntax#backslash
class MarkdownEscapeUtils {
    private MarkdownEscapeUtils() {}

    private static final char[] characters = "\\`*_{}[]()#+-.!".toCharArray();

    public static String escape(String text) {
        String cleanedText = text;
        for (char character : characters) {
            cleanedText = StringUtils.replace(cleanedText, String.valueOf(character), "\\" + character);
        }
        return cleanedText;
    }
}