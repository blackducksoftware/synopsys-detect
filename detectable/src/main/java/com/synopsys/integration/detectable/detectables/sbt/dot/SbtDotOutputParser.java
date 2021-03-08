package com.synopsys.integration.detectable.detectables.sbt.dot;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

public class SbtDotOutputParser {
    public List<File> parseGeneratedGraphFiles(List<String> dotOutput) {
        List<File> graphs = new ArrayList<>();

        for (String line : dotOutput) {
            String potentialFile = parseDotGraphFromLine(line);
            if (potentialFile != null) {
                graphs.add(new File(potentialFile));
            }
        }

        return graphs;
    }

    @Nullable
    private String parseDotGraphFromLine(String line) {
        final String DOT_PREFIX = "[info] Wrote dependency graph to '";
        if (line.startsWith(DOT_PREFIX)) {
            final String DOT_SUFFIX = "'";
            return StringUtils.substringBetween(line, DOT_PREFIX, DOT_SUFFIX);
        } else {
            return null;
        }
    }
}
