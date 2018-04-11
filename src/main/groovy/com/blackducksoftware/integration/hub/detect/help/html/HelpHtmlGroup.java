package com.blackducksoftware.integration.hub.detect.help.html;

import java.util.List;

public class HelpHtmlGroup {
    public String groupName;
    public List<HelpHtmlOption> options;
    
    public String getGroupName() {
        return groupName;
    }
    
    public List<HelpHtmlOption> getOptions() {
        return options;
    }
}