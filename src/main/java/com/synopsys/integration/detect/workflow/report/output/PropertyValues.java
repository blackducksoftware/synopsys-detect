package com.synopsys.integration.detect.workflow.report.output;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class PropertyValues {
    private Map<String, String> map;

    public PropertyValues(final Map<String, String> map) {
        this.map = map;
    }

    public Map<String, String> getMap() {
        return map;
    }

    public Map<String, String> getSortedMap() {
        return map.entrySet().stream()
                   .sorted(Map.Entry.comparingByKey())
                   .collect(Collectors.toMap(
                       Map.Entry::getKey,
                       Map.Entry::getValue,
                       (oldValue, newValue) -> oldValue, LinkedHashMap::new));

    }
}
