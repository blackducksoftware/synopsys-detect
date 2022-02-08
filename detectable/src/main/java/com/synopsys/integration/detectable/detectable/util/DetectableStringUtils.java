package com.synopsys.integration.detectable.detectable.util;

public class DetectableStringUtils {
    public static String removeEvery(String line, String[] targets) {
        int indexToCut = line.length();
        for (String target : targets) {
            if (line.contains(target)) {
                indexToCut = line.indexOf(target);
            }
        }

        return line.substring(0, indexToCut);
    }

    public static int parseIndentationLevel(String line, String indentation) {
        String consumableLine = line;
        int level = 0;

        while (consumableLine.startsWith(indentation)) {
            consumableLine = consumableLine.replaceFirst(indentation, "");
            level++;
        }

        return level;
    }
}
