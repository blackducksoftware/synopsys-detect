package com.synopsys.integration.detectable.detectables.yarn.parse;

import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YarnLockLineAnalyzer {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final static int SPACES_INDENT_PER_LEVEL = 2;

    @NotNull
    public StringTokenizer createKeyValueTokenizer(String line) {
        return createSpaceSeparatedTokenizer(line);
    }

    @NotNull
    public StringTokenizer createKeyListTokenizer(String line) {
        return new StringTokenizer(line.trim(), " :");
    }

    @NotNull
    public StringTokenizer createDependencySpecTokenizer(String line) {
        return createSpaceSeparatedTokenizer(line);
    }

    public int measureIndentDepth(String line) {
        if (StringUtils.isBlank(line)) {
            return 0;
        }
        int leadingSpaceCount = countLeadingSpaces(line);
        if ((leadingSpaceCount % SPACES_INDENT_PER_LEVEL) != 0) {
            logger.warn("Leading space count for '{}' is {}; expected it to be divisible by {}",
                line, leadingSpaceCount, SPACES_INDENT_PER_LEVEL);
        }
        return countLeadingSpaces(line) / SPACES_INDENT_PER_LEVEL;
    }

    @NotNull
    private StringTokenizer createSpaceSeparatedTokenizer(String line) {
        return new StringTokenizer(line.trim(), " ");
    }

    private int countLeadingSpaces(String line) {
        int leadingSpaceCount = 0;
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == ' ') {
                leadingSpaceCount++;
            } else if (line.charAt(i) == '\t') {
                logger.warn("yarn.lock indented with tabs; assuming 1 tab is equivalent to 4 spaces");
                leadingSpaceCount += 4;
            } else {
                break;
            }
        }
        return leadingSpaceCount;
    }
}
