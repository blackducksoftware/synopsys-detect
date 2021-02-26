/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectable.util;

public class DetectableStringUtils {
    public static String removeEvery(final String line, final String[] targets) {
        int indexToCut = line.length();
        for (final String target : targets) {
            if (line.contains(target)) {
                indexToCut = line.indexOf(target);
            }
        }

        return line.substring(0, indexToCut);
    }

    public static int parseIndentationLevel(final String line, String indentation) {
        String consumableLine = line;
        int level = 0;

        while (consumableLine.startsWith(indentation)) {
            consumableLine = consumableLine.replaceFirst(indentation, "");
            level++;
        }

        return level;
    }
}
