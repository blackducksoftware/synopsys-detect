package com.blackducksoftware.integration.hub.detect.help.html;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.blackducksoftware.integration.hub.detect.help.DetectOption;

public class HelpHtmlDataBuilder {

    private Map<String, HelpHtmlGroup> groupsByName = new HashMap<>();
    
    public HelpHtmlDataBuilder addDetectOption(DetectOption option) {
        String groupName = option.getGroup();
        if (!groupsByName.containsKey(groupName)) {
            HelpHtmlGroup group = new HelpHtmlGroup();
            group.groupName = groupName;
            group.options = new ArrayList<>();
            groupsByName.put(groupName, group);
        }
        
        HelpHtmlGroup group = groupsByName.get(groupName);
        HelpHtmlOption htmlOption = new HelpHtmlOption(option.getKey(), option.getDefaultValue(), option.getDescription());
        group.options.add(htmlOption);
        return this;
    }
    
    
    public HelpHtmlData build() {
        return new HelpHtmlData(new ArrayList<>(groupsByName.values()));
    }
}
