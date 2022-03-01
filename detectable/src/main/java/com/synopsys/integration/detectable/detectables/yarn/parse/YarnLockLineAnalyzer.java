package com.synopsys.integration.detectable.detectables.yarn.parse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YarnLockLineAnalyzer {
    private static final int SPACES_INDENT_PER_LEVEL = 2;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public int measureIndentDepth(String line) {
        if (StringUtils.isBlank(line)) {
            return 0;
        }
        int leadingSpaceCount = countLeadingSpaces(line);
        if ((leadingSpaceCount % SPACES_INDENT_PER_LEVEL) != 0) {
            logger.warn("Leading space count for '{}' is {}; expected it to be divisible by {}", line, leadingSpaceCount, SPACES_INDENT_PER_LEVEL);
        }
        return leadingSpaceCount / SPACES_INDENT_PER_LEVEL;
    }

    public String unquote(String s) {
        while (isQuoted(s)) {
            s = s.substring(1, s.length() - 1);
        }
        return s;
    }

    public boolean isQuoted(String line) {
        return isSingleQuotedWith(line, "\"") || isSingleQuotedWith(line, "'");
    }

    private boolean isSingleQuotedWith(String line, String quoteChar) {
        int quoteCount = StringUtils.countMatches(line, quoteChar);
        return (quoteCount == 2) && line.startsWith(quoteChar) && line.endsWith(quoteChar);
    }

    private int countLeadingSpaces(String line) {
        int leadingSpaceCount = 0;
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == ' ') {
                leadingSpaceCount++;
            } else if (line.charAt(i) == '\t') {
                leadingSpaceCount += 4;
            } else {
                break;
            }
        }
        return leadingSpaceCount;
    }
}
