package com.synopsys.integration.detectable.detectables.gradle.inspection.parse;

import java.util.Collections;
import java.util.List;

public class GradleGavPieces {
    private final List<String> resolvedGavPieces;
    private final List<String> replacedGavPieces;

    public static GradleGavPieces createGav(List<String> resolvedGavPieces) {
        return new GradleGavPieces(resolvedGavPieces, Collections.emptyList());
    }

    public static GradleGavPieces createGavWithReplacement(List<String> resolvedGavPieces, List<String> replacedGavPieces) {
        return new GradleGavPieces(resolvedGavPieces, replacedGavPieces);
    }

    private GradleGavPieces(List<String> resolvedGavPieces, List<String> replacedGavPieces) {
        this.resolvedGavPieces = resolvedGavPieces;
        this.replacedGavPieces = replacedGavPieces;
    }

    public List<String> getGavPieces() {
        return resolvedGavPieces;
    }

    public List<String> getReplacedGavPieces() {
        return replacedGavPieces;
    }
}
