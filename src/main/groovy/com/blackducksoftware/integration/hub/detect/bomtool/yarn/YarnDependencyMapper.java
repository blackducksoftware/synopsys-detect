package com.blackducksoftware.integration.hub.detect.bomtool.yarn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

public class YarnDependencyMapper extends BaseYarnParser {

    private final Map<String, String> resolvedVersions = new HashMap<>();

    public void getYarnDataAsMap(List<String> inputLines) {
        List<String> thisDependency = new ArrayList<>();
        String thisVersion;

        for (String line : inputLines) {
            if (StringUtils.isBlank(line)) {
                continue;
            }

            String trimmedLine = line.trim();

            if (trimmedLine.startsWith("#")) {
                continue;
            }

            int level = getLineLevel(line);
            if (level == 0) {
                thisDependency = cleanAndSplit(line);
                continue;
            }

            if (level == 1 && trimmedLine.startsWith("version")) {
                thisVersion = trimmedLine.split(" ")[1].replaceAll("\"", "");
                for (String dep : thisDependency) {
                    resolvedVersions.put(dep, thisVersion);
                }
                resolvedVersions.put(thisDependency.get(0).split("@")[0] + "@" + thisVersion, thisVersion);
            }
        }
    }

    public Optional<String> getVersion(String key) {
        if (resolvedVersions.containsKey(key)) {
            String value = resolvedVersions.get(key);
            if (StringUtils.isNotBlank(value)) {
                return Optional.of(value);
            } else {
                return Optional.empty();
            }
        } else {
            String name = key.split("@")[0];
            for (String fuzzy : resolvedVersions.keySet()) {
                String fullResolvedName = name + "@" + resolvedVersions.get(fuzzy);
                boolean versionHasAlreadyBeenResolvedByYarnList = fuzzy.equals(fullResolvedName);
                if (versionHasAlreadyBeenResolvedByYarnList) {
                    String value = resolvedVersions.get(fuzzy);
                    if (StringUtils.isNotBlank(value)) {
                        return Optional.of(value);
                    } else {
                        return Optional.empty();
                    }
                }
            }
        }
        return Optional.empty();
    }

    public Map<String, String> getResolvedVersions() {
        return resolvedVersions;
    }

    private List<String> cleanAndSplit(String s) {
        List<String> lines = Arrays.asList(s.split(","));
        List<String> result = new ArrayList<>();

        for (String l : lines) {
            result.add(l.trim().replaceAll("\"", "").replaceAll(":", ""));
        }

        return result;
    }

}
