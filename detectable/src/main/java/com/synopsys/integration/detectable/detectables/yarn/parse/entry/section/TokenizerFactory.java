package com.synopsys.integration.detectable.detectables.yarn.parse.entry.section;

import java.util.StringTokenizer;

import org.jetbrains.annotations.NotNull;

public class TokenizerFactory {

    private TokenizerFactory() {}

    @NotNull
    public static StringTokenizer createHeaderTokenizer(String line) {
        return new StringTokenizer(line.trim(), ",");
    }

    @NotNull
    public static StringTokenizer createKeyValueTokenizer(String line) {
        return createColonAndSpaceSeparatedTokenizer(line);
    }

    @NotNull
    public static StringTokenizer createKeyListTokenizer(String line) {
        return new StringTokenizer(line.trim(), " :");
    }

    @NotNull
    public static StringTokenizer createDependencySpecTokenizer(String line) {
        return createColonAndSpaceSeparatedTokenizer(line);
    }

    @NotNull
    private static StringTokenizer createColonAndSpaceSeparatedTokenizer(String line) {
        return new StringTokenizer(line.trim(), ": ");
    }
    
    // yarn 1           - zen-observable@^0.8.0, zen-observable@^0.8.14:
    // resolved version -   version "0.8.15"
    // yarn 3           - "yargs@npm:^16.1.0, yargs@npm:^16.2.0":
    // resolved version -   version: 16.2.0
}
