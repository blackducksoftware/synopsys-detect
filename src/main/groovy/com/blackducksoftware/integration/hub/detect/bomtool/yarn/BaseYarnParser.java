package com.blackducksoftware.integration.hub.detect.bomtool.yarn;

public abstract class BaseYarnParser {

    protected int getLineLevel(String line) {
        int level = 0;
        String tmpLine = line;
        while (tmpLine.startsWith("  ")) {
            tmpLine = tmpLine.replaceFirst("  ", "");
            level++;
        }

        return level;
    }
}
