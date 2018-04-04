package com.blackducksoftware.integration.hub.detect.help.print;

import java.util.Map;

import com.blackducksoftware.integration.hub.detect.help.DetectOption;

public class DetailedDetectOptions {
    
    public Map<String, DetailedDetectOption> options;
    
    public DetailedDetectOption getOptionDetailsOrNull(DetectOption option) {
        if (options.containsKey(option.getKey())) {
            return options.get(option.getKey());
        }
        return null;
    }
    
    public class DetailedDetectOption {
        public String useCases = "";
        public String issues = "";        
    }
}
