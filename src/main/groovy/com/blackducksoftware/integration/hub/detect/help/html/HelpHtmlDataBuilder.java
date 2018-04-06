package com.blackducksoftware.integration.hub.detect.help.html;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.blackducksoftware.integration.hub.detect.help.DetectOption;

public class HelpHtmlDataBuilder {

    private Map<String, HelpHtmlGroup> groupsByName = new HashMap<>();
    
    public HelpHtmlDataBuilder addDetectOption(DetectOption option) {
        String groupName = option.getHelp().primaryGroup;
        if (!groupsByName.containsKey(groupName)) {
            HelpHtmlGroup group = new HelpHtmlGroup();
            group.groupName = groupName;
            group.options = new ArrayList<>();
            groupsByName.put(groupName, group);
        }
        
        HelpHtmlGroup group = groupsByName.get(groupName);
        
        String description = option.getHelp().description;
        if (option.getAcceptableValues().size() > 0) {
            description += " (" + option.getAcceptableValues().stream().collect(Collectors.joining("|")) + ")";
        }
        HelpHtmlOption htmlOption = new HelpHtmlOption(option.getKey(), option.getDefaultValue(), description);
        group.options.add(htmlOption);
        return this;
    }
    
    
    public HelpHtmlData build() {
        return new HelpHtmlData(new ArrayList<>(groupsByName.values()));
    }
}
