/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
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
}
