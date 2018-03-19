package com.blackducksoftware.integration.hub.detect.help.print;

import java.util.List;

import com.blackducksoftware.integration.hub.detect.help.DetectOption;

public class GroupOptionListing {
    String groupName;
    List<DetectOption> detectOptions;

    public GroupOptionListing(final String groupName, final List<DetectOption> detectOptions) {
        super();
        this.groupName = groupName;
        this.detectOptions = detectOptions;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(final String groupName) {
        this.groupName = groupName;
    }

    public List<DetectOption> getDetectOptions() {
        return detectOptions;
    }

    public void setDetectOptions(final List<DetectOption> detectOptions) {
        this.detectOptions = detectOptions;
    }

}
