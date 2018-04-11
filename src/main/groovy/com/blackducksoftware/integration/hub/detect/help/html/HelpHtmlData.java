package com.blackducksoftware.integration.hub.detect.help.html;

import java.util.List;

public class HelpHtmlData {
    public List<HelpHtmlGroup> groups;
    
    public HelpHtmlData(List<HelpHtmlGroup> groups) { 
        this.groups = groups;
    }
    
    public List<HelpHtmlGroup> getGroups() {
        return groups;
    }
}
