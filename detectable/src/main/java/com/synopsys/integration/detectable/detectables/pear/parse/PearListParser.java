/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.pear.parse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.exception.IntegrationException;

public class PearListParser {
    private static final String START_TOKEN = "=========";

    /**
     * @param pearListLines
     * @return A map of package names to their resolved versions
     */
    public Map<String, String> parse(final List<String> pearListLines) throws IntegrationException {
        final Map<String, String> dependenciesMap = new HashMap<>();

        boolean started = false;
        for (final String rawLine : pearListLines) {
            final String line = rawLine.trim();

            if (!started) {
                started = line.startsWith(START_TOKEN);
                continue;
            } else if (StringUtils.isBlank(line) || line.startsWith("Package")) {
                continue;
            }

            final String[] entry = line.split(" +");
            if (entry.length < 2) {
                throw new IntegrationException("Unable to parse pear list");
            }

            final String packageName = entry[0].trim();
            final String packageVersion = entry[1].trim();

            dependenciesMap.put(packageName, packageVersion);
        }

        return dependenciesMap;
    }
}
