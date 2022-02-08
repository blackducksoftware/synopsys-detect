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
    public Map<String, String> parse(List<String> pearListLines) throws IntegrationException {
        Map<String, String> dependenciesMap = new HashMap<>();

        boolean started = false;
        for (String rawLine : pearListLines) {
            String line = rawLine.trim();

            if (!started) {
                started = line.startsWith(START_TOKEN);
                continue;
            } else if (StringUtils.isBlank(line) || line.startsWith("Package")) {
                continue;
            }

            String[] entry = line.split(" +");
            if (entry.length < 2) {
                throw new IntegrationException("Unable to parse pear list");
            }

            String packageName = entry[0].trim();
            String packageVersion = entry[1].trim();

            dependenciesMap.put(packageName, packageVersion);
        }

        return dependenciesMap;
    }
}
