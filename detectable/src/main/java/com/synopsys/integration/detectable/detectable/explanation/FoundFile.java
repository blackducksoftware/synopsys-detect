package com.synopsys.integration.detectable.detectable.explanation;

import java.io.File;

public class FoundFile extends Explanation {
    private final File file;

    public FoundFile(File file) {
        this.file = file;
    }

    @Override
    public String describeSelf() {
        return "Found file: " + file.toString();
    }
}
