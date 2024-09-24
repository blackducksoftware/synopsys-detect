package com.blackduck.integration.detectable.python.util;

public class PythonDependency {
    private final String name;
    private final String version;
    private boolean isConditional;

    public PythonDependency(String name, String version) {
        this.name = name;
        this.version = version;
        this.isConditional = false;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public boolean isConditional() {
        return isConditional;
    }
    
    public void setConditional(boolean isConditional) {
        this.isConditional = isConditional;
    }
}
