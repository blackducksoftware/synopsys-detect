package com.synopsys.integration.detectable.detectable.util;

public class DetectableStringUtils {
    public static String removeEvery(String line, String[] targets) {
        int indexToCut = line.length();
        for (final String target : targets) {
            if (line.contains(target)) {
                indexToCut = line.indexOf(target);
            }
        }

        return line.substring(0, indexToCut);
    }
}
