package com.synopsys.integration.detect.workflow.status;

import java.io.File;
import java.util.List;

public class UnrecognizedPaths {
    private final String group;
    private final List<File> messages;

    public UnrecognizedPaths(String group, List<File> messages) {
        this.group = group;
        this.messages = messages;
    }

    public String getGroup() {
        return group;
    }

    public List<File> getPaths() {
        return messages;
    }
}
