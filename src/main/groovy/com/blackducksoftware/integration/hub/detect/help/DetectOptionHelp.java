package com.blackducksoftware.integration.hub.detect.help;

import java.util.ArrayList;
import java.util.List;

public class DetectOptionHelp {
    
    public String description = "";
    public String useCases = "";
    public String issues = "";

    public List<String> groups = new ArrayList<String>();
    public String primaryGroup = "";
    
    public boolean isDeprecated = false; 
    public String deprecation = "";
    public String deprecationVersion = "";
    
}
