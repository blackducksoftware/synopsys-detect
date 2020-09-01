package com.synopsys.integration.detect.lifecycle.run.data;

import java.util.HashMap;
import java.util.Map;

import com.synopsys.integration.detect.DetectTool;

public class FeatureKeyToolMap {

    private static Map<String, DetectTool> map = populateMap();

    private static Map<String, DetectTool> populateMap() {
        Map<String, DetectTool> map = new HashMap<>();

        map.put("ISCAN", DetectTool.SIGNATURE_SCAN);
        map.put("BINARY_ANALYSIS", DetectTool.BINARY_SCAN);

        return map;
    }

    public static DetectTool getTool(String key) {
        return map.get(key);
    }
}
