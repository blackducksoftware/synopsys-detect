package com.synopsys.integration.detectable.detectable.explanation;

import java.io.File;

public class FoundFile extends Explanation {
    private final String file;

    public FoundFile(File file) {
        this.file = file.toString();
    }

    public FoundFile(String file) {
        this.file = file;
    }

    @Override
    public String describeSelf() {
        return "Found file: " + file;
    }
}
