package com.synopsys.integration.detectable.detectables.conan.cli.parser;

import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConanInfoLineAnalyzer {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @NotNull
    public StringTokenizer createTokenizer(String line) {
        return new StringTokenizer(line.trim(), ":");
    }

    public int measureIndentDepth(String line) {
        if (StringUtils.isBlank(line)) {
            return 0;
        }
        int leadingSpaceCount = countLeadingSpaces(line);
        if ((leadingSpaceCount % 4) != 0) {
            logger.warn("Leading space count for '{}' is {}; expected it to be divisible by 4", line, leadingSpaceCount);
        }
        return countLeadingSpaces(line) / 4;
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
