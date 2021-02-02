package com.synopsys.integration.detectable.detectable.explanation;

import java.io.File;

public class FoundInspector extends Explanation {
    private final File file;

    public FoundInspector(File file) {
        this.file = file;
    }

    @Override
    public String describeSelf() {
        return "Found inspector: " + file.toString();
    }
}
