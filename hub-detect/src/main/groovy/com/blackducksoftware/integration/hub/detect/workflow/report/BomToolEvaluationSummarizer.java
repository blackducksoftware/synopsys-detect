package com.blackducksoftware.integration.hub.detect.workflow.report;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.blackducksoftware.integration.hub.detect.workflow.bomtool.BomToolEvaluation;

public abstract class BomToolEvaluationSummarizer {

    protected Map<File, List<BomToolEvaluation>> groupByDirectory(final List<BomToolEvaluation> results) {
        return results.stream()
                .collect(Collectors.groupingBy(item -> item.getEnvironment().getDirectory()));
    }

    protected int filesystemCompare(final String left, final String right) {
        final String[] pieces1 = left.split(Pattern.quote(File.separator));
        final String[] pieces2 = right.split(Pattern.quote(File.separator));
        final int min = Math.min(pieces1.length, pieces2.length);
        for (int i = 0; i < min; i++) {
            final int compared = pieces1[i].compareTo(pieces2[i]);
            if (compared != 0) {
                return compared;
            }
        }
        return Integer.compare(pieces1.length, pieces2.length);
    }
}
