package com.blackducksoftware.integration.hub.detect.help;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FieldWarnings {

    public List<FieldWarning> warnings = new ArrayList<>();
    
    public void addWarning(String fieldName, String description) {
        warnings.add(new FieldWarning(fieldName, description));
    }
    
    public List<FieldWarning> warningsForField(String fieldName) {
        return warnings.stream().filter(it -> it.fieldName.equals(fieldName)).collect(Collectors.toList());
    }
    
    public List<FieldWarning> getWarnings() {
        return warnings.stream().sorted((o1, o2)->o1.fieldName.compareTo(o2.fieldName)).collect(Collectors.toList());
    }
    
    public class FieldWarning {       
        public String fieldName;
        public String description;
        
        public FieldWarning(String fieldName, String description) {
            this.fieldName = fieldName;
            this.description = description;
        }
    }
    
}
