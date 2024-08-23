package com.synopsys.integration.detectable.detectable.util;

import java.util.Comparator;

public class SemVerComparator implements Comparator<String> {
    @Override
    public int compare(String v1, String v2) {
        // Split each version string into parts
        String[] v1Parts = v1.split("\\.");
        String[] v2Parts = v2.split("\\.");

        // Determine the maximum length to iterate over
        int maxLength = Math.max(v1Parts.length, v2Parts.length);

        // Compare each part until we know which string is smallest
        for (int i = 0; i < maxLength; i++) {
            int part1 = (i < v1Parts.length) ? Integer.parseInt(v1Parts[i]) : 0;
            int part2 = (i < v2Parts.length) ? Integer.parseInt(v2Parts[i]) : 0;

            int comparison = Integer.compare(part1, part2);

            // If the parts are not equal, return the comparison result
            if (comparison != 0) {
                return comparison;
            }
        }

        // If all parts are equal, return 0
        return 0;
    }
}